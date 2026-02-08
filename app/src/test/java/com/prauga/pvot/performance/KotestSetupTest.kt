// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.performance

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

/**
 * Verification test to ensure Kotest property testing is properly configured.
 * This test validates that the Kotest dependencies are working correctly.
 */
class KotestSetupTest : StringSpec({
    
    "kotest property testing is configured correctly" {
        checkAll(10, Arb.int(1..100)) { value ->
            value shouldBe value
        }
    }
    
    "kotest assertions work correctly" {
        val result = 2 + 2
        result shouldBe 4
    }
})
