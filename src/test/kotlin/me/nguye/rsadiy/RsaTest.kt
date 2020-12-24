package me.nguye.rsadiy

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints
import me.nguye.number.BigInt
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
@ExperimentalUnsignedTypes
class RsaTest : WordSpec({
    "(2790, 413, 3233)" When {
        "decrypt" should {
            "returns 65 in base 2^k from 1 to 31" {
                checkAll(Exhaustive.ints(1..3)) { iteration ->
                    val writer = File("output_2790_413_3233_2to64_$iteration.txt").printWriter()
                    writer.use { out ->
                        checkAll(Exhaustive.ints(1..31)) { k ->
                            val c = BigInt.valueOf("101011100110", 2).toBase2PowK(k)
                            val d = BigInt.valueOf("110011101", 2).toBase2PowK(k)
                            val n = BigInt.valueOf("110010100001", 2).toBase2PowK(k)
                            val expected = BigInt.valueOf("1000001", 2).toBase2PowK(k)

                            var result: BigInt
                            measureTime {
                                result = Rsa.decrypt(c, d, n)
                            }.also {
                                println("Base 2^$k, m = $result, Time elapsed: $it")
                                out.println("$k\t${it.toLongNanoseconds()}")
                            }

                            result shouldBe expected
                        }
                    }
                }
            }
        }
    }

    "(Big, Big, Big)" When {
        timeout = Long.MAX_VALUE
        "decrypt" should {
            timeout = Long.MAX_VALUE
            "returns a good result in base 2^k from 1 to 31" {
                timeout = Long.MAX_VALUE
                checkAll(Exhaustive.ints(1..3)) { iteration ->
                    val writer = File("output_Big_2to64_$iteration.txt").printWriter()
                    writer.use { out ->
                        checkAll(Exhaustive.ints(1..31)) { k ->
                            val c = BigInt.valueOf("10100101100111110010110010110101010011101011001111000011011001000010011101100101011011101000101101010011101010011000000110110000111011110110000001001100110111000001101110011101001100111010011110111001110000110110001001000001001011001100001101010100101110110101001000000110111101100101010111111101010011001110100001100100101101111100101010111111100001111101110010111110111010010000110110011010100110110101101001000011000101111000001100001111010011011100100001101000111100011010001101101100001110000100100100100101001101111001010010101100100101111100100100100001111100010111011110010001001001110001110011111001010001010000101001100000101000011101111110000000100000100010000010011101001111111001111001101011111101000011000110111111100000001101110111001010011011000100100110011011110010111110101001110011000101011101111001011101010110000100011011001101001101100100101010111010011010101011000110100000111001011110010111111101010100111010110010101010111101101110110011011011010001111000011101010110011001010110100111110000101110", 2).toBase2PowK(k)
                            val d = BigInt.valueOf("1001010000101110001100010101110110001001100011101010011110010011010011110010101110001100001000110011111000000101001010011110011111010100111000110010101100100000011001100111100111101011101110100011000111010001100011111000000000111111000001110111110000111010110010010101100110010010001001101010000000100111100111111010110011110001000010111001100101011000010100000111101011001111011111100010111101000011100000010001111001101001111010010000101001001101000110000101111010010110001011010010000100010010010000000010010001011111111101001111101110011000011100110111001100011101000001100101010111111110010101011001111011010010111111110011110010010100000100101011000110100110010011001011001110101010010100010000101001001111010111011010101010011100000000010100000100001010111011010000000101001000001011110100100100110101010001011011110111100000101011101001011110001111100101110010101100111001110111000111011010010001101101100111110000000110110101100100010110100001011001000101000100011110110110100000110010101011011010100110100011011101", 2).toBase2PowK(k)
                            val n = BigInt.valueOf("10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001101110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010101001111011", 2).toBase2PowK(k)
                            val expected = BigInt.valueOf("1111011", 2).toBase2PowK(k)

                            var result: BigInt
                            measureTime {
                                result = Rsa.decrypt(c, d, n)
                            }.also {
                                println("Base 2^$k, m = $result, Time elapsed: $it")
                                out.println("$k\t${it.toLongNanoseconds()}")
                            }

                            result shouldBe expected
                        }
                    }
                }
            }
        }
    }
})
