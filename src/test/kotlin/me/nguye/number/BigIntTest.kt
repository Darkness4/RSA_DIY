package me.nguye.number

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

@ExperimentalUnsignedTypes
class BigIntTest : WordSpec({
    "plus" should {
        "-2 + 2 = 0" {
            val a = BigInt.valueOf("-2", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("0", 10)
            a + b shouldBe c
        }
        "5 + 2 = 7" {
            val a = BigInt.valueOf("5", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("7", 10)
            a + b shouldBe c
        }
        "5 + -2 = 3" {
            val a = BigInt.valueOf("5", 10)
            val b = BigInt.valueOf("-2", 10)
            val c = BigInt.valueOf("3", 10)
            a + b shouldBe c
        }
        "-5 + 2 = -3" {
            val a = BigInt.valueOf("-5", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("-3", 10)
            a + b shouldBe c
        }
        "-5 + -2 = -7" {
            val a = BigInt.valueOf("-5", 10)
            val b = BigInt.valueOf("-2", 10)
            val c = BigInt.valueOf("-7", 10)
            a + b shouldBe c
        }
    }
    "div" should {
        "5 / 2 = 2" {
            val five = BigInt.valueOf("5", 10)
            val two = BigInt.valueOf("2", 10)
            five / two shouldBe two
        }

        "34 / 2 = 17" {
            val a = BigInt.valueOf("34", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("17", 10)
            a / b shouldBe c
        }

        "17 / 2 = 8" {
            val a = BigInt.valueOf("17", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("8", 10)
            a / b shouldBe c
        }

        "8 / 2 = 4" {
            val a = BigInt.valueOf("8", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("4", 10)
            a / b shouldBe c
        }

        "4 / 2 = 2" {
            val a = BigInt.valueOf("4", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("2", 10)
            a / b shouldBe c
        }

        "2 / 2 = 1" {
            val a = BigInt.valueOf("2", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("1", 10)
            a / b shouldBe c
        }

        "1 / 2 = 0" {
            val a = BigInt.valueOf("1", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("0", 10)
            a / b shouldBe c
        }
    }

    "rem" should {
        "2 % 2 = 0" {
            val a = BigInt.valueOf("2", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("0", 10)
            a % b shouldBe c
        }
    }

    "times" should {
        "2 * 1 = 2" {
            val a = BigInt.valueOf("2", 10)
            val b = BigInt.valueOf("1", 10)
            val c = BigInt.valueOf("2", 10)
            a * b shouldBe c
        }

        "2 * -1 = -2" {
            val a = BigInt.valueOf("2", 10)
            val b = BigInt.valueOf("-1", 10)
            val c = BigInt.valueOf("-2", 10)
            a * b shouldBe c
        }

        "-2 * 1 = -2" {
            val a = BigInt.valueOf("-2", 10)
            val b = BigInt.valueOf("1", 10)
            val c = BigInt.valueOf("-2", 10)
            a * b shouldBe c
        }

        "-2 * -1 = 2" {
            val a = BigInt.valueOf("-2", 10)
            val b = BigInt.valueOf("-1", 10)
            val c = BigInt.valueOf("2", 10)
            a * b shouldBe c
        }
    }

    "minus" should {
        "2 - 2 = 0" {
            val a = BigInt.valueOf("2", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("0", 10)
            a - b shouldBe c
        }
        "5 - 2 = 3" {
            val a = BigInt.valueOf("5", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("3", 10)
            a - b shouldBe c
        }
        "5 - -2 = 7" {
            val a = BigInt.valueOf("5", 10)
            val b = BigInt.valueOf("-2", 10)
            val c = BigInt.valueOf("7", 10)
            a - b shouldBe c
        }
        "-5 - 2 = -7" {
            val a = BigInt.valueOf("-5", 10)
            val b = BigInt.valueOf("2", 10)
            val c = BigInt.valueOf("-7", 10)
            a - b shouldBe c
        }
        "-5 - -2 = -3" {
            val a = BigInt.valueOf("-5", 10)
            val b = BigInt.valueOf("-2", 10)
            val c = BigInt.valueOf("-3", 10)
            a - b shouldBe c
        }
    }

    "toBase" should {
        "convert to Base2 successfully" {
            // Arrange
            val number = BigInt.valueOf("34", 10)

            // Act
            val result = number.toBase(2u)

            // Assert
            result shouldBe BigInt.valueOf("100010", 2)
        }

        "convert to Base16 successfully" {
            // Arrange
            val number = BigInt.valueOf("55", 10)

            // Act
            val result = number.toBase(16u)

            // Assert
            result shouldBe BigInt.valueOf("37", 16)
        }

        "convert to Base8 successfully" {
            // Arrange
            val number = BigInt.valueOf("55", 10)

            // Act
            val result = number.toBase(8u)

            // Assert
            result shouldBe BigInt.valueOf("67", 8)
        }
    }

    "fromBase2toBase" should {
        "convert to Base16 successfully" {
            // Arrange
            val number = BigInt.valueOf("34", 10)

            // Act
            val result = number.fromBase2toBase(16u)

            // Assert
            result shouldBe BigInt.valueOf("22", 16)
        }

        "convert to Base8 successfully" {
            // Arrange
            val number = BigInt.valueOf("34", 10)

            // Act
            val result = number.fromBase2toBase(8u)

            // Assert
            result shouldBe BigInt.valueOf("42", 8)
        }
    }

    "modPow" should {
        "2790 ^ 413 % 3233 = 65" {
            val c = BigInt.valueOf("2790", 10)
            val d = BigInt.valueOf("413", 10)
            val n = BigInt.valueOf("3233", 10)

            val expected = BigInt.valueOf("65", 10)
            c.modPow(d, n) shouldBe expected.toBase(2u)
        }
    }

    "modInverse" should {
        "10 * x = 1 mod 17 gives 12" {
            val a = BigInt.valueOf("10", 10)
            val b = BigInt.valueOf("17", 10)

            val expected = BigInt.valueOf("12", 10)
            a modInverse b shouldBe expected
        }
    }

    "extendedGCD" should {
        "10 extendedGCD 17 gives 1, -5, 3" {
            val a = BigInt.valueOf("10", 10)
            val b = BigInt.valueOf("17", 10)

            val expected = Triple(BigInt.valueOf("1", 10),
                BigInt.valueOf("-5", 10),
                BigInt.valueOf("3", 10))
            a extendedGCD b shouldBe expected
        }
    }
})
