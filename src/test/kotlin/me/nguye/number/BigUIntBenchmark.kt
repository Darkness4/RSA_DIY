package me.nguye.number

import io.kotest.core.spec.style.WordSpec
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.measureNanoTime
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
@ExperimentalUnsignedTypes
class BigUIntBenchmark : WordSpec({
    "+" should {
        var a = BigUInt.randomWithByteLength(128)
        var b = BigUInt.randomWithByteLength(128)
        a + b

        val result = mutableListOf<Long>()

        for (i in 1..100) {
            a = BigUInt.randomWithByteLength(128)
            b = BigUInt.randomWithByteLength(128)
            result.add(
                measureNanoTime {
                    a + b
                }
            )
        }
        val mean = result.average()
        val stddev = result
            .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
            .let { sqrt(it / result.size) }
        println("${mean.toDuration(DurationUnit.NANOSECONDS)} ± ${stddev.toDuration(DurationUnit.NANOSECONDS)}")
    }

    "-" should {
        var a = BigUInt.randomWithByteLength(128)
        var b = BigUInt.randomWithByteLength(128)

        a - b

        val result = mutableListOf<Long>()

        for (i in 1..100) {
            a = BigUInt.randomWithByteLength(128)
            b = BigUInt.randomWithByteLength(128)
            result.add(
                measureNanoTime {
                    a - b
                }
            )
        }
        val mean = result.average()
        val stddev = result
            .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
            .let { sqrt(it / result.size) }
        println("${mean.toDuration(DurationUnit.NANOSECONDS)} ± ${stddev.toDuration(DurationUnit.NANOSECONDS)}")
    }

    "*" should {
        var a = BigUInt.randomWithByteLength(128)
        var b = BigUInt.randomWithByteLength(128)

        a * b

        val result = mutableListOf<Long>()

        for (i in 1..100) {
            a = BigUInt.randomWithByteLength(128)
            b = BigUInt.randomWithByteLength(128)
            result.add(
                measureNanoTime {
                    a * b
                }
            )
        }
        val mean = result.average()
        val stddev = result
            .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
            .let { sqrt(it / result.size) }
        println("${mean.toDuration(DurationUnit.NANOSECONDS)} ± ${stddev.toDuration(DurationUnit.NANOSECONDS)}")
    }

    "montgomeryTimes" should {
        var a = BigUInt.randomWithByteLength(128)
        var b = BigUInt.randomWithByteLength(128)
        val n =
            BigUInt.valueOf("179769313486231590772930519078902473361797697894230657273430081157732675805500963132708477322407536021120113879871393357658789768814416622492847430639477074095512480796227391561801824887394139579933613278628104952355769470429079061808809522886423955917442317693387325171135071792698344550223571732405562649211")

        a.montgomeryTimes(b, n)

        val result = mutableListOf<Long>()

        for (i in 1..100) {
            a = BigUInt.randomWithByteLength(128)
            b = BigUInt.randomWithByteLength(128)

            result.add(
                measureNanoTime {
                    a.montgomeryTimes(b, n)
                }
            )
        }
        val mean = result.average()
        val stddev = result
            .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
            .let { sqrt(it / result.size) }
        println("${mean.toDuration(DurationUnit.NANOSECONDS)} ± ${stddev.toDuration(DurationUnit.NANOSECONDS)}")
    }

    "modPow" should {
        var a = BigUInt.randomWithByteLength(128)
        var b = BigUInt.randomWithByteLength(128)
        val n =
            BigUInt.valueOf("179769313486231590772930519078902473361797697894230657273430081157732675805500963132708477322407536021120113879871393357658789768814416622492847430639477074095512480796227391561801824887394139579933613278628104952355769470429079061808809522886423955917442317693387325171135071792698344550223571732405562649211")

        a.modPow(b, n)

        val result = mutableListOf<Long>()

        for (i in 1..100) {
            a = BigUInt.randomWithByteLength(128)
            b = BigUInt.randomWithByteLength(128)
            result.add(
                measureNanoTime {
                    a.modPow(b, n)
                }
            )
        }
        val mean = result.average()
        val stddev = result
            .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
            .let { sqrt(it / result.size) }
        println("${mean.toDuration(DurationUnit.NANOSECONDS)} ± ${stddev.toDuration(DurationUnit.NANOSECONDS)}")
    }
})
