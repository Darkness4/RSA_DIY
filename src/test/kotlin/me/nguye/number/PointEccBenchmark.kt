package me.nguye.number

import io.kotest.core.spec.style.WordSpec
import me.nguye.ecc.curves.Secp192r1
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.measureNanoTime
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
@ExperimentalUnsignedTypes
class PointEccBenchmark : WordSpec({
    "*" should {
        Secp192r1.generator * BigUInt.valueOf("0")

        val result = mutableListOf<Long>()

        for (i in 1..100) {
            result.add(
                measureNanoTime {
                    Secp192r1.generator * BigUInt.valueOf(100.toString())
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
