// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.keypad

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The default phone keypad keys. */
class PvotKeypadKeysTest {

    @Test
    fun twelveKeysInPhoneOrder() {
        val digits = PvotKeypadDefaults.keys().map { it.digit }
        assertEquals(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#"), digits)
    }

    @Test
    fun aKeyReadsItsDigitAndLetters() {
        val keys = PvotKeypadDefaults.keys()
        assertEquals("2, ABC", keys[1].contentDescription)
        assertEquals("1", keys[0].contentDescription)
        assertEquals("#", keys[11].contentDescription)
    }

    @Test
    fun plusShowsUnderZeroOnlyWithItsLongPress() {
        assertNull(PvotKeypadDefaults.keys()[10].letters)
        val zero = PvotKeypadDefaults.keys(zeroLongPressLabel = "Plus")[10]
        assertEquals("+", zero.letters)
        assertEquals("Plus", zero.longPressLabel)
    }

    @Test
    fun otherDigitsReplaceTheWesternOnes() {
        val persian = listOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        val keys = PvotKeypadDefaults.keys(digits = persian)
        assertEquals("۱", keys[0].digit)
        assertEquals("۰", keys[10].digit)
        assertEquals("*", keys[9].digit)
    }

    @Test(expected = IllegalArgumentException::class)
    fun keysNeedsTenDigits() {
        PvotKeypadDefaults.keys(digits = listOf("0"))
    }
}
