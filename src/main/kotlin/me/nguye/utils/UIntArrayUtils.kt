package me.nguye.utils

import kotlin.math.ceil
import kotlin.math.log2

@ExperimentalUnsignedTypes
fun UIntArray.stripTrailingZero(): UIntArray {
    // Find first nonzero byte
    var keep = this.size - 1
    while (keep > 0 && this[keep] == 0u) {
        keep--
    }
    return if (keep == this.size - 1) this else this.copyOfRange(0, keep + 1)
}

/**
 * Considering that UIntArray is a array of digit. Return this / 2
 *
 * Algorithm description:
 * `(a_0 + a_1 * base + ... + a_n * base^n)/2` can be developed to
 * `a_0/2 + (a_1 * base)/2 + ... + (a_n * base^n)/2`. If one of the division has a carry (i.e is impair), then
 *  this carry will pass to the i - 1 th element.
 *
 * So, to summarize:
 * -  Loop from the nth element to the zeroth element
 *    - Add the carry if exist
 *    - Store the new carry if impair
 *    - Divide by 2 the element
 */
@ExperimentalUnsignedTypes
fun UIntArray.divBy2(radix: UInt): UIntArray {
    val zero = uintArrayOf(0u)
    val one = uintArrayOf(1u)
    if (this.contentEquals(zero) || this.contentEquals(one)) return zero
    val result = this.copyOf()

    var carry = 0u
    for (i in size - 1 downTo 0) {
        result[i] = result[i] + carry
        carry = if (result[i] % 2u == 1u) radix else 0u // Store carry if remainder exist
        result[i] = result[i] shr 1 // Div by 2
    }

    return result
}

/**
 * Convert BigInt magnitude array to base 2.
 *
 * Algorithm description: Division method.
 */
@ExperimentalUnsignedTypes
fun UIntArray.toBase2Array(radix: UInt): UIntArray {
    val size = ceil(this.size * log2(radix.toDouble())).toInt()
    var result = UIntArray(size)
    val zero = uintArrayOf(0u)

    var i = 0
    var num = this
    while (!num.contentEquals(zero)) {
        if (i >= result.size) {  // BufferOverflow. Allocate more !  // TODO: Error
            result = result.copyOf(i + size)
        }
        result[i] = num[0] % 2u // num % 2
        num = num.divBy2(radix).stripTrailingZero()
        i++
    }

    return result
}

/**
 * Convert a array in [radix] to an array in base 2.pow(k)
 *
 * Algorithm Description :
 * -  Convert the source in binary
 * -  Combine chunks of digit into one
 *
 * Self-Explanatory Example with *137 to base 16 (2^4)*:
 * -  137 = 0b10001001
 * -  0b10001001 = 1000 | 1001 = 0x89
 */
@ExperimentalUnsignedTypes
fun UIntArray.toBase2PowK(radix: UInt, k: Int): UIntArray {
    val thisBase2 = this.toBase2Array(radix)
    val result = UIntArray(size = thisBase2.size / k + 1)

    for (chunkIndex in result.indices) {
        for (offset in 0 until k) { // k = chunckSize
            // result[chunkIndex] += x * 2.pow(offset)
            // x is a bit. x = thisBase2.mag[chunkIndex * k + offset]
            // If thisBase2.mag[chunkIndex * k + offset] fails, it returns 0u.
            result[chunkIndex] += thisBase2.elementAtOrElse(chunkIndex * k + offset) { 0u } shl offset
        }
    }

    return result
}
