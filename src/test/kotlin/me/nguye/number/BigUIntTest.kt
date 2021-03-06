package me.nguye.number

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
class BigUIntTest : WordSpec({
    "valueOf(\"2147483648\")" should {
        "have a magnitude array of {0, 1}" {
            val result = BigUInt.valueOf("2147483648")
            result.mag shouldContainExactly uintArrayOf(0u, 1u)
        }
    }
    "basePowK" should {
        "BASE^5" {
            val result = BigUInt.basePowK(5)
            result shouldBe BigUInt.valueOf("45671926166590716193865151022383844364247891968")
        }
    }
    "plus" should {
        "(-2)u + 2u = 4u" {
            val a = BigUInt.valueOf("-2")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("4")
            a + b shouldBe c
        }
        "5 + 2 = 7" {
            val a = BigUInt.valueOf("5")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("7")
            a + b shouldBe c
        }
        "5u + (-2)u = 7" {
            val a = BigUInt.valueOf("5")
            val b = BigUInt.valueOf("-2")
            val c = BigUInt.valueOf("7")
            a + b shouldBe c
        }
        "(-5)u + 2 = 7" {
            val a = BigUInt.valueOf("-5")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("7")
            a + b shouldBe c
        }
        "(-5)u + (-2)u = 7" {
            val a = BigUInt.valueOf("-5")
            val b = BigUInt.valueOf("-2")
            val c = BigUInt.valueOf("7")
            a + b shouldBe c
        }
    }
    "div" should {
        "5 / 2 = 2" {
            val five = BigUInt.valueOf("5")
            val two = BigUInt.valueOf("2")
            five / two shouldBe two
        }

        "34 / 2 = 17" {
            val a = BigUInt.valueOf("34")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("17")
            a / b shouldBe c
        }

        "17 / 2 = 8" {
            val a = BigUInt.valueOf("17")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("8")
            a / b shouldBe c
        }

        "8 / 2 = 4" {
            val a = BigUInt.valueOf("8")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("4")
            a / b shouldBe c
        }

        "4 / 2 = 2" {
            val a = BigUInt.valueOf("4")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("2")
            a / b shouldBe c
        }

        "2 / 2 = 1" {
            val a = BigUInt.valueOf("2")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("1")
            a / b shouldBe c
        }

        "1 / 2 = 0" {
            val a = BigUInt.valueOf("1")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("0")
            a / b shouldBe c
        }
    }

    "rem" should {
        "2 % 2 = 0" {
            val a = BigUInt.valueOf("2")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("0")
            a % b shouldBe c
        }
    }

    "times" should {
        "2 * 1 = 2" {
            val a = BigUInt.valueOf("2")
            val b = BigUInt.valueOf("1")
            val c = BigUInt.valueOf("2")
            a * b shouldBe c
        }

        "2 * -1 = -2" {
            val a = BigUInt.valueOf("2")
            val b = BigUInt.valueOf("-1")
            val c = BigUInt.valueOf("-2")
            a * b shouldBe c
        }

        "-2 * 1 = -2" {
            val a = BigUInt.valueOf("-2")
            val b = BigUInt.valueOf("1")
            val c = BigUInt.valueOf("-2")
            a * b shouldBe c
        }

        "-2 * -1 = 2" {
            val a = BigUInt.valueOf("-2")
            val b = BigUInt.valueOf("-1")
            val c = BigUInt.valueOf("2")
            a * b shouldBe c
        }
    }

    "minus" should {
        "a big test" {
            val a = BigUInt.valueOf("808875172023299381901557550838302821130338410270580636008982357881317354281187997317869881030460954848582284738871129203205600146945640092904963665578531589125263001195483294096872093504962369395313524529571035503935061702407655921750293249367261267921442131495070963773234146007713429261")
            val b = BigUInt.valueOf("1755970830787437532315416671127600607726693207842870621243005171975753603031289555787722136465977484152188228099276287568356720088142559119114143753072786916596539728540202452777687574007946809795603513227909933836740120471643310392957013918919785376996612467816483856321662579978157546473")
            val c = BigUInt.valueOf("947095658764138150413859120289297786596354797572289985234022814094436248750101558469852255435516529303605943360405158365151119941196919026209180087494255327471276727344719158680815480502984440400289988698338898332805058769235654471206720669552524109075170336321412892548428433970444117212")
            a - b shouldBe c
        }
        "2 - 2 = 0" {
            val a = BigUInt.valueOf("2")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("0")
            a - b shouldBe c
        }
        "5 - 2 = 3" {
            val a = BigUInt.valueOf("5")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("3")
            a - b shouldBe c
        }
        "5u - (-2)u = 3u" {
            val a = BigUInt.valueOf("5")
            val b = BigUInt.valueOf("-2")
            val c = BigUInt.valueOf("3")
            a - b shouldBe c
        }
        "(-5)u - 2u = 3" {
            val a = BigUInt.valueOf("-5")
            val b = BigUInt.valueOf("2")
            val c = BigUInt.valueOf("3")
            a - b shouldBe c
        }
        "(-5)u - (-2)u = 3" {
            val a = BigUInt.valueOf("-5")
            val b = BigUInt.valueOf("-2")
            val c = BigUInt.valueOf("3")
            a - b shouldBe c
        }
    }

    "modPow" should {
        "2790 ^ 2 % 7 = 2" {
            val c = BigUInt.valueOf("2790")
            val d = BigUInt.valueOf("2")
            val n = BigUInt.valueOf("7")

            val expected = BigUInt.valueOf("2")
            c.modPow(d, n) shouldBe expected
        }

        "2790 ^ 413 % 3233 = 65 from base 36" {
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
            val c = BigUInt.valueOf("2790")
            val d = BigUInt.valueOf("413")
            val n = BigUInt.valueOf("3233")

            val expected = BigUInt.valueOf("65")
            c.modPow(d, n) shouldBe expected
        }
    }

    "modInverse" should {
        "3233 modInverse BASE = 1889" {
            val n = BigUInt.valueOf("3233")
            val r = BigUInt.basePowK(n.mag.size)
            val v = n modInverse r

            v shouldBe BigUInt.valueOf("425776993")
        }
    }

    "{1, 2, 3} magShl 2 should" should {
        "returns {3}" {
            val a = BigUInt(uintArrayOf(1u, 2u, 3u))
            val expected = BigUInt(uintArrayOf(3u))
            val result = a magShl 2
            result shouldBe expected
        }
    }

    "{1, 2, 3} remMagShl 2 should" should {
        "returns {1, 2}" {
            val a = BigUInt(uintArrayOf(1u, 2u, 3u))
            val expected = BigUInt(uintArrayOf(1u, 2u))
            val result = a remMagShl 2
            result shouldBe expected
        }
    }

    "modTimes" should {
        "17 modTimes 7 % 11 = 9" {
            val a = BigUInt.valueOf("17")
            val b = BigUInt.valueOf("7")
            val n = BigUInt.valueOf("11")
            val expected = BigUInt.valueOf("9")

            val result = a.modTimes(b, n)

            result shouldBe expected
        }

        "17 * 7 % 11 = 9" {
            val a = BigUInt.valueOf("17")
            val b = BigUInt.valueOf("7")
            val n = BigUInt.valueOf("11")
            val expected = BigUInt.valueOf("9")

            val result = (a * b) % n

            result shouldBe expected
        }
    }

    "montgomeryTimes" should {
        "A to phi(A) with A = 413 * BASE mod 3233 = 882" {
            val a = BigUInt.valueOf("413")
            val n = BigUInt.valueOf("3233")

            val rSquare = BigUInt.basePowK(n.mag.size * 2) % n
            val aMgy = a.montgomeryTimes(rSquare, n)

            aMgy shouldBe BigUInt.valueOf("882")
        }

        "phi(A) to A with A = 413 * BASE mod 3233 = 882" {
            val a = BigUInt.valueOf("413")
            val n = BigUInt.valueOf("3233")

            val rSquare = BigUInt.basePowK(n.mag.size * 2) % n
            val aMgy = a.montgomeryTimes(rSquare, n)
            val aNotMgy = aMgy.montgomeryTimes(BigUInt.one, n)

            aNotMgy shouldBe a
        }
    }
})
