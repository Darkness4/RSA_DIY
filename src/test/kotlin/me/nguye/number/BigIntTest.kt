package me.nguye.number

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

@ExperimentalUnsignedTypes
class BigIntTest : WordSpec({
    "div" should {
        "5 / 2 = 2" {
            val five = BigUInt.valueOf("5", 10)
            val two = BigUInt.valueOf("2", 10)
            five / two shouldBe two
        }

        "34 / 2 = 17" {
            val a = BigUInt.valueOf("34", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("17", 10)
            a / b shouldBe c
        }

        "17 / 2 = 8" {
            val a = BigUInt.valueOf("17", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("8", 10)
            a / b shouldBe c
        }

        "8 / 2 = 4" {
            val a = BigUInt.valueOf("8", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("4", 10)
            a / b shouldBe c
        }

        "4 / 2 = 2" {
            val a = BigUInt.valueOf("4", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("2", 10)
            a / b shouldBe c
        }

        "2 / 2 = 1" {
            val a = BigUInt.valueOf("2", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("1", 10)
            a / b shouldBe c
        }

        "1 / 2 = 0" {
            val a = BigUInt.valueOf("1", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("0", 10)
            a / b shouldBe c
        }
    }

    "rem" should {
        "2 % 2 = 0" {
            val a = BigUInt.valueOf("2", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("0", 10)
            a % b shouldBe c
        }
    }

    "times" should {
        "2 * 1 = 2" {
            val a = BigUInt.valueOf("2", 10)
            val b = BigUInt.valueOf("1", 10)
            val c = BigUInt.valueOf("2", 10)
            a * b shouldBe c
        }
    }

    "minus" should {
        "2 - 2 = 0" {
            val a = BigUInt.valueOf("2", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("0", 10)
            a - b shouldBe c
        }
    }

    "toBase" should {
        "convert to Base2 successfully" {
            // Arrange
            val number = BigUInt.valueOf("34", 10)

            // Act
            val result = number.toBase(2u)

            // Assert
            result shouldBe BigUInt.valueOf("100010", 2)
        }

        "convert to Base16 successfully" {
            // Arrange
            val number = BigUInt.valueOf("55", 10)

            // Act
            val result = number.toBase(16u)

            // Assert
            result shouldBe BigUInt.valueOf("37", 16)
        }

        "convert to Base8 successfully" {
            // Arrange
            val number = BigUInt.valueOf("55", 10)

            // Act
            val result = number.toBase(8u)

            // Assert
            result shouldBe BigUInt.valueOf("67", 8)
        }
    }

    "fromBase2toBase" should {
        "convert to Base16 successfully" {
            // Arrange
            val number = BigUInt.valueOf("34", 10)

            // Act
            val result = number.fromBase2toBase(16u)

            // Assert
            result shouldBe BigUInt.valueOf("22", 16)
        }

        "convert to Base8 successfully" {
            // Arrange
            val number = BigUInt.valueOf("34", 10)

            // Act
            val result = number.fromBase2toBase(8u)

            // Assert
            result shouldBe BigUInt.valueOf("42", 8)
        }
    }

    "modPow" should {
        "2790 ^ 413 % 3233 = 65" {
            val c = BigUInt.valueOf("2790", 10)
            val d = BigUInt.valueOf("413", 10)
            val n = BigUInt.valueOf("3233", 10)

            val expected = BigUInt.valueOf("1000001", 2)
            c.modPow(d, n) shouldBe expected
        }
    }
})
