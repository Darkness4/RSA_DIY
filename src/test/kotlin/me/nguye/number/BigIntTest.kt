package me.nguye.number

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
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
        "2 ^ 23 % 55 = 8" {
            val c = BigInt.valueOf("2", 10)
            val d = BigInt.valueOf("23", 10)
            val n = BigInt.valueOf("55", 10)

            val expected = BigInt.valueOf("8", 10)
            c.modPow(d, n) shouldBe expected.toBase(2u)
        }
    }

    "montgomeryTimes" should {
        "A to phi(A) with A = 413 * 10000 mod 3233 = 1459 in base 10" {
            val a = BigInt.valueOf("413", 10)
            val n = BigInt.valueOf("3233", 10)

            val r = BigInt.basePowK(10u, n.mag.size)
            val rSquare = BigInt.basePowK(10u, n.mag.size * 2)
            val (gcd, rPrime, v) = r extendedGcd n
            val negativeV = BigInt(v.mag, v.base, -v.sign)

            val aMgy = a.montgomeryTimes(rSquare, n, negativeV)

            gcd shouldBe BigInt.one(10u)
            aMgy shouldBe BigInt.valueOf("1459", 10)
        }

        "phi(A) to A with A = 413 * 10000 mod 3233 = 1459 in base 10" {
            val a = BigInt.valueOf("413", 10)
            val n = BigInt.valueOf("3233", 10)

            val r = BigInt.basePowK(10u, n.mag.size)
            val rSquare = BigInt.basePowK(10u, n.mag.size * 2)
            val (gcd, rPrime, v) = r extendedGcd n
            val negativeV = BigInt(v.mag, v.base, -v.sign)

            val aMgy = a.montgomeryTimes(rSquare, n, negativeV)
            val aNotMgy = aMgy.montgomeryTimes(BigInt.one(base = 10u), n, negativeV)

            gcd shouldBe BigInt.one(10u)
            aNotMgy shouldBe a
        }

        "A to phi(A) with A = 413 * 4096 mod 3233 = 789" {
            val a = BigInt.valueOf("413", 10).toBase(2u)
            val n = BigInt.valueOf("3233", 10).toBase(2u)

            // Convert to base 2
            val r = BigInt.twoPowK(n.mag.size)
            val rSquare = BigInt.twoPowK(n.mag.size * 2)
            val (gcd, rPrime, v) = r extendedGcd n
            val negativeV = BigInt(v.mag, v.base, -v.sign)

            val aMgy = a.montgomeryTimes(rSquare, n, negativeV)

            gcd shouldBe BigInt.one(2u)
            aMgy shouldBe BigInt.valueOf("789", 10).toBase(2u)
        }

        "phi(A) to A with A = 413 * 4096 mod 3233" {
            val a = BigInt.valueOf("413", 10).toBase(2u)
            val n = BigInt.valueOf("3233", 10).toBase(2u)

            val r = BigInt.twoPowK(n.mag.size)
            val rSquare = BigInt.twoPowK(n.mag.size * 2)
            val (gcd, rPrime, v) = r extendedGcd n
            val negativeV = BigInt(v.mag, v.base, -v.sign)

            val aMgy = a.montgomeryTimes(rSquare, n, negativeV)
            val aNotMgy = aMgy.montgomeryTimes(BigInt.one(base = 2u), n, negativeV)

            gcd shouldBe BigInt.one(2u)
            aNotMgy shouldBe a
        }
    }

    "remShl" should {
        "17 remShl 3 = 1 in base 2 (equivalent to mod 8)" {
            val a = BigInt.valueOf("17", 10).toBase(2u)
            val c = BigInt.valueOf("1", 10).toBase(2u)
            a remShl 3 shouldBe c
        }

        "32 remShl 1 = 2 in base 10 (equivalent to mod 10)" {
            val a = BigInt.valueOf("32", 10)
            val c = BigInt.valueOf("2", 10)
            a remShl 1 shouldBe c
        }

        "314 remShl 2 = 14 in base 10 (equivalent to mod 100)" {
            val a = BigInt.valueOf("314", 10)
            val c = BigInt.valueOf("14", 10)
            a remShl 2 shouldBe c
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
            a extendedGcd b shouldBe expected
        }
    }
})
