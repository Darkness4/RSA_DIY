package me.nguye.rsadiy

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.booleans.shouldBeTrue
import me.nguye.ecc.curves.Secp192r1
import me.nguye.ecc.curves.Secp256k1
import me.nguye.ecc.curves.Secp521r1
import me.nguye.number.PointEcc
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.measureNanoTime
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
@ExperimentalUnsignedTypes
class EdchTest : WordSpec({
    "Smoke test" should {
        "work" {
            val session = Edch(Secp192r1)
            val alicePrivateKey = session.generatePrivateKey()
            val alicePublicKey = session.generatePublicKey(alicePrivateKey)
            println("Alice private key: $alicePrivateKey")
            println("Alice public key: $alicePublicKey")

            val bobPrivateKey = session.generatePrivateKey()
            val bobPublicKey = session.generatePublicKey(bobPrivateKey)
            println("Bob private key: $bobPrivateKey")
            println("Bob public key: $bobPublicKey")

            println("Key Exchange !!!")

            val aliceSharedKey = session.generateSharedKey(bobPublicKey, alicePrivateKey)
            val bobSharedKey = session.generateSharedKey(alicePublicKey, bobPrivateKey)

            println("Equality : ${aliceSharedKey == bobSharedKey}")
        }
    }

    "fakeKeyExchange" should {
        "work on Secp192r1" {
            val session = Edch(Secp192r1)

            session.fakeKeyExchange().shouldBeTrue()
        }

        "work on Secp256k1" {
            val session = Edch(Secp256k1)

            session.fakeKeyExchange().shouldBeTrue()
        }

        "work on Secp521r1" {
            val session = Edch(Secp521r1)

            session.fakeKeyExchange().shouldBeTrue()
        }

        "benchmark Secp192r1" {
            val session = Edch(Secp192r1)

            println(session.fakeKeyExchange())

            val resultPublicKey = mutableListOf<Long>()
            val resultSharedKey = mutableListOf<Long>()

            for (i in 1..25) {
                val alicePrivateKey = session.generatePrivateKey()
                var alicePublicKey: PointEcc
                measureNanoTime {
                    alicePublicKey = session.generatePublicKey(alicePrivateKey)
                }.also {
                    resultPublicKey.add(it)
                }

                val bobPrivateKey = session.generatePrivateKey()
                var bobPublicKey: PointEcc
                measureNanoTime {
                    bobPublicKey = session.generatePublicKey(bobPrivateKey)
                }.also {
                    resultPublicKey.add(it)
                }

                println("Key Exchange !!!")

                measureNanoTime {
                    session.generateSharedKey(bobPublicKey, alicePrivateKey)
                }.also {
                    resultSharedKey.add(it)
                }

                measureNanoTime {
                    session.generateSharedKey(alicePublicKey, bobPrivateKey)
                }.also {
                    resultSharedKey.add(it)
                }
            }

            val mean = resultPublicKey.average()
            val stddev = resultPublicKey
                .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
                .let { sqrt(it / resultPublicKey.size) }
            println("${mean.toDuration(DurationUnit.NANOSECONDS)} ± ${stddev.toDuration(DurationUnit.NANOSECONDS)}")

            val mean2 = resultSharedKey.average()
            val stddev2 = resultSharedKey
                .fold(0.0, { accumulator, next -> accumulator + (next - mean2).pow(2.0) })
                .let { sqrt(it / resultSharedKey.size) }
            println("${mean2.toDuration(DurationUnit.NANOSECONDS)} ± ${stddev2.toDuration(DurationUnit.NANOSECONDS)}")
        }

        "benchmark Secp256k1" {
            val session = Edch(Secp256k1)

            println(session.fakeKeyExchange())

            val resultPublicKey = mutableListOf<Long>()
            val resultSharedKey = mutableListOf<Long>()

            for (i in 1..25) {
                val alicePrivateKey = session.generatePrivateKey()
                var alicePublicKey: PointEcc
                measureNanoTime {
                    alicePublicKey = session.generatePublicKey(alicePrivateKey)
                }.also {
                    resultPublicKey.add(it)
                }

                val bobPrivateKey = session.generatePrivateKey()
                var bobPublicKey: PointEcc
                measureNanoTime {
                    bobPublicKey = session.generatePublicKey(bobPrivateKey)
                }.also {
                    resultPublicKey.add(it)
                }

                println("Key Exchange !!!")

                measureNanoTime {
                    session.generateSharedKey(bobPublicKey, alicePrivateKey)
                }.also {
                    resultSharedKey.add(it)
                }

                measureNanoTime {
                    session.generateSharedKey(alicePublicKey, bobPrivateKey)
                }.also {
                    resultSharedKey.add(it)
                }
            }

            val mean = resultPublicKey.average()
            val stddev = resultPublicKey
                .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
                .let { sqrt(it / resultPublicKey.size) }
            println("${mean.toDuration(DurationUnit.NANOSECONDS)} ± ${stddev.toDuration(DurationUnit.NANOSECONDS)}")

            val mean2 = resultSharedKey.average()
            val stddev2 = resultSharedKey
                .fold(0.0, { accumulator, next -> accumulator + (next - mean2).pow(2.0) })
                .let { sqrt(it / resultSharedKey.size) }
            println("${mean2.toDuration(DurationUnit.NANOSECONDS)} ± ${stddev2.toDuration(DurationUnit.NANOSECONDS)}")
        }

        /*"benchmark Secp521r1" {
            val session = Edch(Secp521r1)

            println(session.fakeKeyExchange())

            val resultPublicKey = mutableListOf<Long>()
            val resultSharedKey = mutableListOf<Long>()

            for (i in 1..5) {
                val alicePrivateKey = session.generatePrivateKey()
                var alicePublicKey: PointEcc
                measureNanoTime {
                    alicePublicKey = session.generatePublicKey(alicePrivateKey)
                }.also {
                    resultPublicKey.add(it)
                }

                val bobPrivateKey = session.generatePrivateKey()
                var bobPublicKey: PointEcc
                measureNanoTime {
                    bobPublicKey = session.generatePublicKey(bobPrivateKey)
                }.also {
                    resultPublicKey.add(it)
                }

                println("Key Exchange !!!")

                measureNanoTime {
                    session.generateSharedKey(bobPublicKey, alicePrivateKey)
                }.also {
                    resultSharedKey.add(it)
                }

                measureNanoTime {
                    session.generateSharedKey(alicePublicKey, bobPrivateKey)
                }.also {
                    resultSharedKey.add(it)
                }
            }

            val mean = resultPublicKey.average()
            val stddev = resultPublicKey
                .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
                .let { sqrt(it / resultPublicKey.size) }
            println("${mean.toDuration(DurationUnit.NANOSECONDS)} ± ${stddev.toDuration(DurationUnit.NANOSECONDS)}")

            val mean2 = resultSharedKey.average()
            val stddev2 = resultSharedKey
                .fold(0.0, { accumulator, next -> accumulator + (next - mean2).pow(2.0) })
                .let { sqrt(it / resultSharedKey.size) }
            println("${mean2.toDuration(DurationUnit.NANOSECONDS)} ± ${stddev2.toDuration(DurationUnit.NANOSECONDS)}")
        }*/
    }
})
