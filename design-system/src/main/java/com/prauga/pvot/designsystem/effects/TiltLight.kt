// SPDX-FileCopyrightText: 2026 The LineageOS Project
// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.effects

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.exp
import kotlin.math.sqrt

internal object TiltLight {

    var x = 0f
        private set

    var y = 0f
        private set

    private val listeners = ArrayList<Runnable>()

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private val handler = Handler(Looper.getMainLooper())

    private var idle = false
    private var smoothing = 0f

    private var rawX = Float.NaN
    private var rawY = 0f
    private var refX = 0f
    private var refY = 0f
    private var prevRawX = 0f
    private var prevRawY = 0f
    private var speed = 0f
    private var stillFor = 0f
    private var settle = 0f
    private var lastFrameNanos = 0L

    private val sensorListener =
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val (gx, gy, gz) = event.values
                val g = sqrt(gx * gx + gy * gy + gz * gz)
                if (g < 1f) return
                val first = rawX.isNaN()
                rawX = gx / g
                rawY = gy / g
                if (first) {
                    refX = rawX
                    refY = rawY
                    prevRawX = rawX
                    prevRawY = rawY
                }
                if (idle && abs(rawX - refX) + abs(rawY - refY) > WAKE_DELTA) {
                    handler.post(::wake)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

    private val frameCallback =
        object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                if (listeners.isEmpty()) return
                Choreographer.getInstance().postFrameCallback(this)
                val dt =
                    if (lastFrameNanos == 0L) 0f
                    else ((frameTimeNanos - lastFrameNanos) / 1e9f).coerceIn(0f, 0.1f)
                lastFrameNanos = frameTimeNanos
                if (rawX.isNaN()) return

                if (dt > 0f) {
                    val moved = abs(rawX - prevRawX) + abs(rawY - prevRawY)
                    speed += (moved / dt - speed) * (1f - exp(-dt / SPEED_SMOOTHING_S))
                }
                prevRawX = rawX
                prevRawY = rawY

                if (speed > STILL_SPEED) {
                    stillFor = 0f
                    settle = 0f
                } else {
                    stillFor += dt
                    if (stillFor >= STILL_DELAY_S) {
                        settle = (settle + dt / SETTLE_EASE_IN_S).coerceAtMost(1f)
                    }
                }
                if (settle > 0f) {
                    val recentre = 1f - exp(-dt * settle / RETURN_S)
                    refX += (rawX - refX) * recentre
                    refY += (rawY - refY) * recentre
                }
                var targetX = -(rawX - refX)
                var targetY = rawY - refY
                val length = sqrt(targetX * targetX + targetY * targetY)
                if (length > 1f) {
                    targetX /= length
                    targetY /= length
                }

                val ease = 1f - exp(-dt / smoothing)
                val newX = x + (targetX - x) * ease
                val newY = y + (targetY - y) * ease
                if (abs(newX - x) + abs(newY - y) < MIN_CHANGE) {
                    if (settle >= 1f && abs(x) + abs(y) < REST_DELTA) goIdle()
                    return
                }
                x = newX
                y = newY
                for (i in listeners.indices.reversed()) listeners[i].run()
            }
        }

    private fun goIdle() {
        val manager = sensorManager ?: return
        val source = sensor ?: return
        if (idle) return
        idle = true
        Choreographer.getInstance().removeFrameCallback(frameCallback)
        if (x != 0f || y != 0f) {
            x = 0f
            y = 0f
            for (i in listeners.indices.reversed()) listeners[i].run()
        }
        manager.unregisterListener(sensorListener)
        manager.registerListener(sensorListener, source, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun wake() {
        val manager = sensorManager ?: return
        val source = sensor ?: return
        if (!idle) return
        idle = false
        speed = 0f
        stillFor = 0f
        settle = 0f
        lastFrameNanos = 0L
        manager.unregisterListener(sensorListener)
        manager.registerListener(sensorListener, source, SensorManager.SENSOR_DELAY_GAME)
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    fun addListener(context: Context, listener: Runnable) {
        if (listener in listeners) return
        if (!ValueAnimator.areAnimatorsEnabled()) return
        listeners += listener
        if (listeners.size == 1) start(context.applicationContext)
    }

    fun removeListener(listener: Runnable) {
        if (!listeners.remove(listener) || listeners.isNotEmpty()) return
        stop()
    }

    private fun start(context: Context) {
        val manager = context.getSystemService(SensorManager::class.java) ?: return
        val gravity = manager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        val sensor = gravity ?: manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: return
        smoothing = if (gravity != null) SMOOTHING_GRAVITY_S else SMOOTHING_ACCELEROMETER_S
        sensorManager = manager
        this.sensor = sensor
        idle = false
        rawX = Float.NaN
        speed = 0f
        stillFor = 0f
        settle = 0f
        lastFrameNanos = 0L
        manager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_GAME)
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    private fun stop() {
        sensorManager?.unregisterListener(sensorListener)
        sensorManager = null
        sensor = null
        idle = false
        handler.removeCallbacksAndMessages(null)
        Choreographer.getInstance().removeFrameCallback(frameCallback)
        x = 0f
        y = 0f
    }

    fun layRimGradient(
        matrix: Matrix,
        left: Float,
        top: Float,
        width: Float,
        height: Float,
        followTilt: Boolean,
    ): Float {
        var lightX = LEVEL_LIGHT
        var lightY = LEVEL_LIGHT
        if (followTilt) {
            lightX += x * TILT_GAIN
            lightY += y * TILT_GAIN
        }
        val strength = sqrt(lightX * lightX + lightY * lightY)
        val angle =
            if (strength > 1e-4f) Math.toDegrees(atan2(-lightY, -lightX).toDouble()).toFloat()
            else 45f
        matrix.setTranslate(-0.5f, 0f)
        matrix.postScale(GRADIENT_SPAN, GRADIENT_SPAN)
        matrix.postRotate(angle)
        matrix.postTranslate(0.5f, 0.5f)
        matrix.postScale(width, height)
        matrix.postTranslate(left, top)
        return strength.coerceAtMost(1f)
    }

    private const val LEVEL_LIGHT = -0.7071f
    private const val TILT_GAIN = 5f
    private const val GRADIENT_SPAN = 1.4142f

    private const val STILL_SPEED = 0.05f
    private const val STILL_DELAY_S = 0.35f
    private const val SETTLE_EASE_IN_S = 0.4f
    private const val RETURN_S = 0.5f
    private const val SPEED_SMOOTHING_S = 0.15f
    private const val SMOOTHING_GRAVITY_S = 0.08f
    private const val SMOOTHING_ACCELEROMETER_S = 0.16f
    private const val MIN_CHANGE = 1e-4f
    private const val REST_DELTA = 2e-3f
    private const val WAKE_DELTA = 0.02f
}
