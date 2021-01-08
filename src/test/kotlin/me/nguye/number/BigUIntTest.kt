package me.nguye.number

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
@ExperimentalUnsignedTypes
class BigUIntTest : WordSpec({
    "basePowK" should {
        "2^5" {
            val result = BigUInt.basePowK(2u, 5)
            result shouldBe BigUInt.valueOf("32", 10).toBase2()
        }
    }
    "plus" should {
        "(-2)u + 2u = 4u" {
            val a = BigUInt.valueOf("-2", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("4", 10)
            a + b shouldBe c
        }
        "5 + 2 = 7" {
            val a = BigUInt.valueOf("5", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("7", 10)
            a + b shouldBe c
        }
        "5u + (-2)u = 7" {
            val a = BigUInt.valueOf("5", 10)
            val b = BigUInt.valueOf("-2", 10)
            val c = BigUInt.valueOf("7", 10)
            a + b shouldBe c
        }
        "(-5)u + 2 = 7" {
            val a = BigUInt.valueOf("-5", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("7", 10)
            a + b shouldBe c
        }
        "(-5)u + (-2)u = 7" {
            val a = BigUInt.valueOf("-5", 10)
            val b = BigUInt.valueOf("-2", 10)
            val c = BigUInt.valueOf("7", 10)
            a + b shouldBe c
        }
    }
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
        "{127839} * {127839} = {97601, 124685} in base 2^17" {
            val a = BigUInt(uintArrayOf(127839u), 131072u)
            val b = BigUInt(uintArrayOf(127839u), 131072u)
            val c = BigUInt(uintArrayOf(97601u, 124685u), 131072u)
            a * b shouldBe c
        }

        "2 * 1 = 2" {
            val a = BigUInt.valueOf("2", 10)
            val b = BigUInt.valueOf("1", 10)
            val c = BigUInt.valueOf("2", 10)
            a * b shouldBe c
        }

        "2 * -1 = -2" {
            val a = BigUInt.valueOf("2", 10)
            val b = BigUInt.valueOf("-1", 10)
            val c = BigUInt.valueOf("-2", 10)
            a * b shouldBe c
        }

        "-2 * 1 = -2" {
            val a = BigUInt.valueOf("-2", 10)
            val b = BigUInt.valueOf("1", 10)
            val c = BigUInt.valueOf("-2", 10)
            a * b shouldBe c
        }

        "-2 * -1 = 2" {
            val a = BigUInt.valueOf("-2", 10)
            val b = BigUInt.valueOf("-1", 10)
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
        "5 - 2 = 3" {
            val a = BigUInt.valueOf("5", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("3", 10)
            a - b shouldBe c
        }
        "5u - (-2)u = 3u" {
            val a = BigUInt.valueOf("5", 10)
            val b = BigUInt.valueOf("-2", 10)
            val c = BigUInt.valueOf("3", 10)
            a - b shouldBe c
        }
        "(-5)u - 2u = 3" {
            val a = BigUInt.valueOf("-5", 10)
            val b = BigUInt.valueOf("2", 10)
            val c = BigUInt.valueOf("3", 10)
            a - b shouldBe c
        }
        "(-5)u - (-2)u = 3" {
            val a = BigUInt.valueOf("-5", 10)
            val b = BigUInt.valueOf("-2", 10)
            val c = BigUInt.valueOf("3", 10)
            a - b shouldBe c
        }
    }

    "toBase2" should {
        "convert to Base2 successfully" {
            val input = BigUInt.valueOf(
                "29075891562236853554062599128328159590183028980552101085544262704780053549534418578507236855720567419975163282590047789761592080601496152946952125090156744601158717295382511431523328696904736152776043566580142204885912443831792077784183631398221826664611547239837936198939692178263167338882034607609865731118",
                10
            )
            val expected = BigUInt.valueOf(
                "10100101100111110010110010110101010011101011001111000011011001000010011101100101011011101000101101010011101010011000000110110000111011110110000001001100110111000001101110011101001100111010011110111001110000110110001001000001001011001100001101010100101110110101001000000110111101100101010111111101010011001110100001100100101101111100101010111111100001111101110010111110111010010000110110011010100110110101101001000011000101111000001100001111010011011100100001101000111100011010001101101100001110000100100100100101001101111001010010101100100101111100100100100001111100010111011110010001001001110001110011111001010001010000101001100000101000011101111110000000100000100010000010011101001111111001111001101011111101000011000110111111100000001101110111001010011011000100100110011011110010111110101001110011000101011101111001011101010110000100011011001101001101100100101010111010011010101011000110100000111001011110010111111101010100111010110010101010111101101110110011011011010001111000011101010110011001010110100111110000101110",
                2
            )

            measureTime {
                val result = input.toBase2()
                result shouldBe expected
            }.also {
                println("Time elapsed: $it")
            }
        }
    }

    "toBase2PowK" should {
        "convert to Base16 successfully" {
            // Arrange
            val number = BigUInt.valueOf("34", 10)

            // Act
            val result = number.toBase2PowK(4)

            // Assert
            result shouldBe BigUInt.valueOf("22", 16)
        }

        "convert to Base8 successfully" {
            // Arrange
            val number = BigUInt.valueOf("34", 10)

            // Act
            val result = number.toBase2PowK(3)

            // Assert
            result shouldBe BigUInt.valueOf("42", 8)
        }

        "benchmark from base 2" {
            timeout = Long.MAX_VALUE
            checkAll(Exhaustive.ints(1..3)) { iteration ->
                val writer = File("toBase2PowK_$iteration.txt").printWriter()
                writer.use { out ->
                    checkAll(Exhaustive.ints(1..31)) { k ->
                        measureTime {
                            BigUInt.valueOf(
                                "10100101100111110010110010110101010011101011001111000011011001000010011101100101011011101000101101010011101010011000000110110000111011110110000001001100110111000001101110011101001100111010011110111001110000110110001001000001001011001100001101010100101110110101001000000110111101100101010111111101010011001110100001100100101101111100101010111111100001111101110010111110111010010000110110011010100110110101101001000011000101111000001100001111010011011100100001101000111100011010001101101100001110000100100100100101001101111001010010101100100101111100100100100001111100010111011110010001001001110001110011111001010001010000101001100000101000011101111110000000100000100010000010011101001111111001111001101011111101000011000110111111100000001101110111001010011011000100100110011011110010111110101001110011000101011101111001011101010110000100011011001101001101100100101010111010011010101011000110100000111001011110010111111101010100111010110010101010111101101110110011011011010001111000011101010110011001010110100111110000101110",
                                2
                            ).toBase2PowK(k)
                        }.also {
                            println("Base 2^$k, Time elapsed: $it")
                            out.println("$k\t${it.toLongNanoseconds()}")
                        }
                    }
                }
            }
        }

        "benchmark from base 10" {
            timeout = Long.MAX_VALUE
            checkAll(Exhaustive.ints(1..3)) { iteration ->
                val writer = File("FromBase10toBase2PowK_$iteration.txt").printWriter()
                writer.use { out ->
                    checkAll(Exhaustive.ints(1..31)) { k ->
                        measureTime {
                            BigUInt.valueOf("29075891562236853554062599128328159590183028980552101085544262704780053549534418578507236855720567419975163282590047789761592080601496152946952125090156744601158717295382511431523328696904736152776043566580142204885912443831792077784183631398221826664611547239837936198939692178263167338882034607609865731118", 10).toBase2PowK(k)
                        }.also {
                            println("Base 2^$k, Time elapsed: $it")
                            out.println("$k\t${it.toLongNanoseconds()}")
                        }
                    }
                }
            }
        }

        "benchmark from base 16" {
            timeout = Long.MAX_VALUE
            checkAll(Exhaustive.ints(1..3)) { iteration ->
                val writer = File("FromBase16toBase2PowK_$iteration.txt").printWriter()
                writer.use { out ->
                    checkAll(Exhaustive.ints(1..31)) { k ->
                        measureTime {
                            BigUInt.valueOf("2967CB2D53ACF0D909D95BA2D4EA606C3BD8133706E74CE9EE70D8904B30D52ED481BD957F533A192DF2AFE1F72FBA4366A6D690C5E0C3D3721A3C68DB0E12494DE52B25F2487C5DE449C73E5142982877E02088274FE79AFD0C6FE037729B1266F2FA9CC577975611B34D92AE9AAC6839797F54EB2ABDBB36D1E1D5995A7C2E", 16).toBase2PowK(k)
                        }.also {
                            println("Base 2^$k, Time elapsed: $it")
                            out.println("$k\t${it.toLongNanoseconds()}")
                        }
                    }
                }
            }
        }
    }

    "modPow" should {
        "2790 ^ 413 % 3233 = 65 in base 2^17" {
            val c = BigUInt.valueOf("25I", 36).toBase2PowK(17)
            val d = BigUInt.valueOf("BH", 36).toBase2PowK(17)
            val n = BigUInt.valueOf("2HT", 36).toBase2PowK(17)

            val expected = BigUInt.valueOf("1T", 36).toBase2PowK(17)
            c.modPow(d, n) shouldBe expected
        }
        "2790 ^ 413 % 3233 = 65 in base 64" {
            val c = BigUInt.valueOf("25I", 36).toBase2PowK(6)
            val d = BigUInt.valueOf("BH", 36).toBase2PowK(6)
            val n = BigUInt.valueOf("2HT", 36).toBase2PowK(6)

            val expected = BigUInt.valueOf("1T", 36).toBase2PowK(6)
            c.modPow(d, n) shouldBe expected
        }
        "2790 ^ 413 % 3233 = 65 in base 36" {
            val c = BigUInt.valueOf("25I", 36)
            val d = BigUInt.valueOf("BH", 36)
            val n = BigUInt.valueOf("2HT", 36)

            val expected = BigUInt.valueOf("1T", 36)
            c.modPow(d, n) shouldBe expected
        }

        "2790 ^ 413 % 3233 = 65 in base 16" {
            val c = BigUInt.valueOf("AE6", 16)
            val d = BigUInt.valueOf("19D", 16)
            val n = BigUInt.valueOf("CA1", 16)

            val expected = BigUInt.valueOf("41", 16)
            c.modPow(d, n) shouldBe expected
        }

        "2790 ^ 413 % 3233 = 65 in base 10" {
            val c = BigUInt.valueOf("2790", 10)
            val d = BigUInt.valueOf("413", 10)
            val n = BigUInt.valueOf("3233", 10)

            val expected = BigUInt.valueOf("65", 10)
            c.modPow(d, n) shouldBe expected
        }

        "2790 ^ 413 % 3233 = 65 in base 2" {
            val c = BigUInt.valueOf("2790", 10).toBase2()
            val d = BigUInt.valueOf("413", 10).toBase2()
            val n = BigUInt.valueOf("3233", 10).toBase2()

            val expected = BigUInt.valueOf("65", 10).toBase2()
            c.modPow(d, n) shouldBe expected
        }

        "2790 ^ 413 % 3233 = 65 in base 100000" {
            val c = BigUInt(uintArrayOf(2790u), 10000u)
            val d = BigUInt(uintArrayOf(413u), 10000u)
            val n = BigUInt(uintArrayOf(3233u), 10000u)

            val expected = BigUInt(uintArrayOf(65u), 10000u)
            c.modPow(d, n) shouldBe expected
        }
    }

    "shl" should {
        "12345 shl 2 in base 10 = 123" {
            val a = BigUInt.valueOf("12345", 10)

            val expected = BigUInt.valueOf("123", 10)
            a shl 2 shouldBe expected
        }
        "12345 shl 4 in base 10 = 1" {
            val a = BigUInt.valueOf("12345", 10)

            val expected = BigUInt.valueOf("1", 10)
            a shl 4 shouldBe expected
        }
        "12345 shl 5 in base 10 = 0" {
            val a = BigUInt.valueOf("12345", 10)

            val expected = BigUInt.valueOf("0", 10)
            a shl 5 shouldBe expected
        }

        "-12345 shl 2 in base 10 = -123" {
            val a = BigUInt.valueOf("-12345", 10)

            val expected = BigUInt.valueOf("-123", 10)
            a shl 2 shouldBe expected
        }
    }

    "modInverse" should {
        "3233 modInverse 4096" {
            val n = BigUInt.valueOf("3233", 10).toBase2()
            val r = BigUInt.basePowK(2u, n.mag.size)
            val v = n modInverse r
            println(v.toLong())

            v shouldBe BigUInt.valueOf("1889", 10).toBase2()
        }

        "3233 modInverse 131072 in base 2^17" {
            val n = BigUInt.valueOf("3233", 10).toBase2PowK(17)
            val r = BigUInt.basePowK(131072u, n.mag.size)
            val v = n modInverse r
            println(v.toLong())

            v shouldBe BigUInt(uintArrayOf(55137u), 131072u)
        }
    }

    "montgomeryTimes" should {
        "A to phi(A) with A = 413 * 10000 mod 3233 = 1459 in base 10" {
            val a = BigUInt.valueOf("413", 10)
            val n = BigUInt.valueOf("3233", 10)

            val r = BigUInt.basePowK(10u, n.mag.size)
            val rSquare = BigUInt.basePowK(10u, n.mag.size * 2) % n
            val v = r - (n modInverse r)

            val aMgy = a.montgomeryTimes(rSquare, n, v)

            aMgy shouldBe BigUInt.valueOf("1459", 10)
        }

        "phi(A) to A with A = 413 * 10000 mod 3233 = 1459 in base 10" {
            val a = BigUInt.valueOf("413", 10)
            val n = BigUInt.valueOf("3233", 10)

            val r = BigUInt.basePowK(10u, n.mag.size)
            val rSquare = BigUInt.basePowK(10u, n.mag.size * 2) % n
            val v = r - (n modInverse r)

            val aMgy = a.montgomeryTimes(rSquare, n, v)
            val aNotMgy = aMgy.montgomeryTimes(BigUInt.one(base = 10u), n, v)

            aNotMgy shouldBe a
        }

        "A to phi(A) with A = 413 * 4096 mod 3233 = 789" {
            val a = BigUInt.valueOf("413", 10).toBase2()
            val n = BigUInt.valueOf("3233", 10).toBase2()

            // Convert to base 2
            val r = BigUInt.basePowK(2u, n.mag.size)
            val rSquare = BigUInt.basePowK(2u, n.mag.size * 2) % n
            val v = r - (n modInverse r)

            val aMgy = a.montgomeryTimes(rSquare, n, v)

            aMgy shouldBe BigUInt.valueOf("789", 10).toBase2()
        }

        "phi(A) to A with A = 413 * 4096 mod 3233" {
            val a = BigUInt.valueOf("413", 10).toBase2()
            val n = BigUInt.valueOf("3233", 10).toBase2()

            val r = BigUInt.basePowK(2u, n.mag.size)
            val rSquare = BigUInt.basePowK(2u, n.mag.size * 2) % n
            val v = r - (n modInverse r)

            val aMgy = a.montgomeryTimes(rSquare, n, v)
            val aNotMgy = aMgy.montgomeryTimes(BigUInt.one(base = 2u), n, v)

            aNotMgy shouldBe a
        }

        "phi(A) to A with A = 413 * 10000 mod 3233 = 1459 in base 2^17" {
            val a = BigUInt.valueOf("413", 10).toBase2PowK(17)
            val n = BigUInt.valueOf("3233", 10).toBase2PowK(17)

            val r = BigUInt.basePowK(131072u, n.mag.size)
            val rSquare = BigUInt.basePowK(131072u, n.mag.size * 2) % n
            val v = r - (n modInverse r)

            val aMgy = a.montgomeryTimes(rSquare, n, v)
            val aNotMgy = aMgy.montgomeryTimes(BigUInt.one(base = 131072u), n, v)

            aNotMgy shouldBe a
        }
    }

    "remShl" should {
        "17 remShl 3 = 1 in base 2 (equivalent to mod 8)" {
            val a = BigUInt.valueOf("17", 10).toBase2()
            val c = BigUInt.valueOf("1", 10).toBase2()
            a remShl 3 shouldBe c
        }

        "32 remShl 1 = 2 in base 10 (equivalent to mod 10)" {
            val a = BigUInt.valueOf("32", 10)
            val c = BigUInt.valueOf("2", 10)
            a remShl 1 shouldBe c
        }

        "314 remShl 2 = 14 in base 10 (equivalent to mod 100)" {
            val a = BigUInt.valueOf("314", 10)
            val c = BigUInt.valueOf("14", 10)
            a remShl 2 shouldBe c
        }

        "4126767 remShl 5 = 26767 in base 10 (equivalent to mod 100000)" {
            val a = BigUInt.valueOf("4126767", 10)
            val c = BigUInt.valueOf("26767", 10)
            a remShl 5 shouldBe c
        }

        "-4126767 remShl 5 = -26767 in base 10 (equivalent to mod 100000)" {
            val a = BigUInt.valueOf("-4126767", 10)
            val c = BigUInt.valueOf("-26767", 10)
            a remShl 5 shouldBe c
        }

        "10 remShl 5 = 10 in base 10 (equivalent to mod 100000)" {
            val a = BigUInt.valueOf("10", 10)
            val c = BigUInt.valueOf("10", 10)
            a remShl 5 shouldBe c
        }

        "100000 remShl 5 = 0 in base 10 (equivalent to mod 100000)" {
            val a = BigUInt.valueOf("100000", 10)
            val c = BigUInt.valueOf("0", 10)

            val result = a remShl 5
            result shouldBe c
        }
    }
})
