package me.nguye.number

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
class BigIntTest : WordSpec({
    "basePowK" should {
        "2^5" {
            val result = BigInt.basePowK(2u, 5)
            result shouldBe BigInt.valueOf("32", 10).toBase2()
        }
    }
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

    "toBase2powK" should {
        "convert to Base16 successfully" {
            // Arrange
            val number = BigInt.valueOf("34", 10)

            // Act
            val result = number.toBase2PowK(4)

            // Assert
            result shouldBe BigInt.valueOf("22", 16)
        }

        "convert to Base8 successfully" {
            // Arrange
            val number = BigInt.valueOf("34", 10)

            // Act
            val result = number.toBase2PowK(3)

            // Assert
            result shouldBe BigInt.valueOf("42", 8)
        }
    }

    "modPow" should {
        "2790 ^ 413 % 3233 = 65 in base 64" {
            val c = BigInt.valueOf("25I", 36).toBase2PowK(6)
            val d = BigInt.valueOf("BH", 36).toBase2PowK(6)
            val n = BigInt.valueOf("2HT", 36).toBase2PowK(6)

            val expected = BigInt.valueOf("1T", 36).toBase2PowK(6)
            c.modPow(d, n) shouldBe expected
        }
        "2790 ^ 413 % 3233 = 65 in base 36" {
            val c = BigInt.valueOf("25I", 36)
            val d = BigInt.valueOf("BH", 36)
            val n = BigInt.valueOf("2HT", 36)

            val expected = BigInt.valueOf("1T", 36)
            c.modPow(d, n) shouldBe expected
        }

        "2790 ^ 413 % 3233 = 65 in base 16" {
            val c = BigInt.valueOf("AE6", 16)
            val d = BigInt.valueOf("19D", 16)
            val n = BigInt.valueOf("CA1", 16)

            val expected = BigInt.valueOf("41", 16)
            c.modPow(d, n) shouldBe expected
        }

        "2790 ^ 413 % 3233 = 65 in base 10" {
            val c = BigInt.valueOf("2790", 10)
            val d = BigInt.valueOf("413", 10)
            val n = BigInt.valueOf("3233", 10)

            val expected = BigInt.valueOf("65", 10)
            c.modPow(d, n) shouldBe expected
        }

        "2790 ^ 413 % 3233 = 65 in base 2" {
            val c = BigInt.valueOf("2790", 10).toBase2()
            val d = BigInt.valueOf("413", 10).toBase2()
            val n = BigInt.valueOf("3233", 10).toBase2()

            val expected = BigInt.valueOf("65", 10).toBase2()
            c.modPow(d, n) shouldBe expected
        }

        "2790 ^ 413 % 3233 = 65 in base 100000" {
            val c = BigInt(uintArrayOf(2790u), 10000u)
            val d = BigInt(uintArrayOf(413u), 10000u)
            val n = BigInt(uintArrayOf(3233u), 10000u)

            val expected = BigInt(uintArrayOf(65u), 10000u)
            c.modPow(d, n) shouldBe expected
        }
    }

    "shl" should {
        "12345 shl 2 in base 10 = 123" {
            val a = BigInt.valueOf("12345", 10)

            val expected = BigInt.valueOf("123", 10)
            a shl 2 shouldBe expected
        }
        "12345 shl 4 in base 10 = 1" {
            val a = BigInt.valueOf("12345", 10)

            val expected = BigInt.valueOf("1", 10)
            a shl 4 shouldBe expected
        }
        "12345 shl 5 in base 10 = 0" {
            val a = BigInt.valueOf("12345", 10)

            val expected = BigInt.valueOf("0", 10)
            a shl 5 shouldBe expected
        }

        "-12345 shl 2 in base 10 = -123" {
            val a = BigInt.valueOf("-12345", 10)

            val expected = BigInt.valueOf("-123", 10)
            a shl 2 shouldBe expected
        }
    }

    "modInverse" should {
        "3233 modInverse 4096" {
            val n = BigInt.valueOf("3233", 10).toBase2()
            val r = BigInt.basePowK(2u, n.mag.size)
            val v = n modInverse r
            println(v.toLong())

            v shouldBe BigInt.valueOf("1889", 10).toBase2()
        }
    }

    "montgomeryTimes" should {
        "A to phi(A) with A = 413 * 10000 mod 3233 = 1459 in base 10" {
            val a = BigInt.valueOf("413", 10)
            val n = BigInt.valueOf("3233", 10)

            val r = BigInt.basePowK(10u, n.mag.size)
            val rSquare = BigInt.basePowK(10u, n.mag.size * 2) % n
            val v = r - (n modInverse r)

            val aMgy = a.montgomeryTimes(rSquare, n, v)

            aMgy shouldBe BigInt.valueOf("1459", 10)
        }

        "phi(A) to A with A = 413 * 10000 mod 3233 = 1459 in base 10" {
            val a = BigInt.valueOf("413", 10)
            val n = BigInt.valueOf("3233", 10)

            val r = BigInt.basePowK(10u, n.mag.size)
            val rSquare = BigInt.basePowK(10u, n.mag.size * 2) % n
            val v = r - (n modInverse r)

            val aMgy = a.montgomeryTimes(rSquare, n, v)
            val aNotMgy = aMgy.montgomeryTimes(BigInt.one(base = 10u), n, v)

            aNotMgy shouldBe a
        }

        "A to phi(A) with A = 413 * 4096 mod 3233 = 789" {
            val a = BigInt.valueOf("413", 10).toBase2()
            val n = BigInt.valueOf("3233", 10).toBase2()

            // Convert to base 2
            val r = BigInt.basePowK(2u, n.mag.size)
            val rSquare = BigInt.basePowK(2u, n.mag.size * 2) % n
            val v = r - (n modInverse r)

            val aMgy = a.montgomeryTimes(rSquare, n, v)

            aMgy shouldBe BigInt.valueOf("789", 10).toBase2()
        }

        "phi(A) to A with A = 413 * 4096 mod 3233" {
            val a = BigInt.valueOf("413", 10).toBase2()
            val n = BigInt.valueOf("3233", 10).toBase2()

            val r = BigInt.basePowK(2u, n.mag.size)
            val rSquare = BigInt.basePowK(2u, n.mag.size * 2) % n
            val v = r - (n modInverse r)

            val aMgy = a.montgomeryTimes(rSquare, n, v)
            val aNotMgy = aMgy.montgomeryTimes(BigInt.one(base = 2u), n, v)

            aNotMgy shouldBe a
        }
    }

    "remShl" should {
        "17 remShl 3 = 1 in base 2 (equivalent to mod 8)" {
            val a = BigInt.valueOf("17", 10).toBase2()
            val c = BigInt.valueOf("1", 10).toBase2()
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

        "4126767 remShl 5 = 26767 in base 10 (equivalent to mod 100000)" {
            val a = BigInt.valueOf("4126767", 10)
            val c = BigInt.valueOf("26767", 10)
            a remShl 5 shouldBe c
        }

        "-4126767 remShl 5 = -26767 in base 10 (equivalent to mod 100000)" {
            val a = BigInt.valueOf("-4126767", 10)
            val c = BigInt.valueOf("-26767", 10)
            a remShl 5 shouldBe c
        }

        "10 remShl 5 = 10 in base 10 (equivalent to mod 100000)" {
            val a = BigInt.valueOf("10", 10)
            val c = BigInt.valueOf("10", 10)
            a remShl 5 shouldBe c
        }

        "100000 remShl 5 = 0 in base 10 (equivalent to mod 100000)" {
            val a = BigInt.valueOf("100000", 10)
            val c = BigInt.valueOf("0", 10)

            val result = a remShl 5
            result shouldBe c
        }
    }
})
