package me.nguye.number

import java.math.BigInteger
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.max

@ExperimentalUnsignedTypes
class BigUInt(mag: UIntArray, val base: UInt) : Comparable<BigUInt> {
    companion object {
        private const val DEFAULT_BASE_STRING = 10

        fun valueOf(str: String, radix: Int = DEFAULT_BASE_STRING): BigUInt {
            val mag = UIntArray(str.length)
            for ((i, char) in str.withIndex()) {
                mag[str.length - i - 1] = Character.digit(char, radix).toUInt()
            }
            return BigUInt(mag, radix.toUInt())
        }

        fun zero(base: UInt) = BigUInt(UIntArray(1) { 0u }, base)
        fun one(base: UInt) = BigUInt(UIntArray(1) { 1u }, base)
        fun two(base: UInt) = BigUInt(UIntArray(1) { 2u }, base)
    }

    private val zero by lazy { zero(base) }
    private val one by lazy { one(base) }
    private val two by lazy { two(base) }

    /**
     * The magnitude of this BigInteger, in <i>little-endian</i> order: the
     * zeroth element of this array is the least-significant int of the
     * magnitude.
     */
    private var mag: UIntArray

    init {
        this.mag = mag.stripTrailingZero()
    }

    operator fun plus(other: BigUInt): BigUInt {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return other
        if (other == zero) return this

        val result = UIntArray(max(mag.size, other.mag.size) + 1)
        var carry: UInt = 0u
        var i = 0

        // Add common parts of both numbers
        while (i < mag.size && i < other.mag.size) {
            val sum: UInt = mag[i] + other.mag[i] + carry
            result[i] = sum % base
            carry = sum / base
            i++
        }

        // Add the last part
        while (i < mag.size) {
            val sum: UInt = mag[i] + carry
            result[i] = sum % base
            carry = sum / base
            i++
        }
        while (i < other.mag.size) {
            val sum: UInt = other.mag[i] + carry
            result[i] = sum % other.base
            carry = sum / base
            i++
        }

        // Add the last carry (if exists)
        if (carry > 0uL) {
            result[i] = carry
        }

        return BigUInt(result, base)
    }

    operator fun minus(other: BigUInt): BigUInt {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return other
        if (other == zero) return this

        val result = UIntArray(max(mag.size, other.mag.size))
        var carry: UInt = 0u

        val (largest, smallest) = if (this < other) {
            other to this
        } else {
            this to other
        }

        // Subtract common parts of both numbers
        for (i in smallest.mag.indices) {
            var sub: UInt = largest.mag[i] - smallest.mag[i] - carry
            carry = if (largest.mag[i] < smallest.mag[i] + carry) {
                sub += largest.base
                1u
            } else 0u
            result[i] = sub
        }

        // Subtract the last part
        for (i in smallest.mag.size until largest.mag.size) {
            var sub: UInt = largest.mag[i] - carry
            carry = if (largest.mag[i] < carry) {
                sub += largest.base
                1u
            } else 0u
            result[i] = sub
        }

        return BigUInt(result, base)
    }

    operator fun times(other: BigUInt): BigUInt {
        if (base != other.base) throw NumberFormatException()
        if (this == zero || other == zero) return zero

        val result = UIntArray(mag.size + other.mag.size)

        // Basic multiplication
        for (i in other.mag.indices) {
            var carry: UInt = 0u
            for (j in mag.indices) {
                result[i + j] += other.mag[i] * mag[j] + carry
                carry = result[i + j] / base
                result[i + j] = result[i + j] % base
            }
            result[i + mag.size] = carry
        }

        return BigUInt(result, base)
    }

    /**
     * Shift the magnitude array to the left. (equivalent to dividing by base, since little endian)
     *
     * 0 are added on the right. No rotation.
     */
    infix fun shl(n: Int): BigUInt {
        if (n == 0) return this

        // An example :
        // n = 2
        // mag = {0, 1, 2, 3, 4, 5, 6} (size = 7)
        // subArray = {2, 3, 4, 5, 6} (size = mag.size - n = 5)
        // zeroes = {0, 0, 0, 0, 0, 0, 0}
        // result = {2, 3, 4, 5, 6, 0, 0} (destinationOffset = n = 2)
        val subArray = mag.copyOfRange(n, mag.size)

        val zeroes = UIntArray(mag.size)

        val result = subArray.copyInto(zeroes, 0, 0, subArray.size)
        return BigUInt(result, base)
    }

    /**
     * Shift the magnitude array to the right. (equivalent to multiplying by base, since little endian)
     *
     * 0 are added on the left. No rotation.
     */
    infix fun shr(n: Int): BigUInt {
        if (n == 0) return this

        // An example :
        // n = 2
        // mag = {0, 1, 2, 3, 4, 5, 6} (size = 7)
        // subArray = {0, 1, 2, 3, 4} (size = mag.size - n = 5)
        // zeroes = {0, 0, 0, 0, 0, 0, 0}
        // result = {0, 0, 0, 1, 2, 3, 4} (destinationOffset = n = 2)
        val subArray = mag.copyOfRange(0, mag.size - n)

        val zeroes = UIntArray(mag.size)

        val result = subArray.copyInto(zeroes, n, 0, subArray.size)
        return BigUInt(result, base)
    }

    fun divBy2(): BigUInt {
        if (this == zero || this == one) return zero
        val result = mag.copyOf()

        var carry: UInt = 0u
        for (i in mag.size - 1 downTo 0) {
            result[i] = result[i] + carry
            carry = if (result[i] % 2u == 1u) base else 0u
            result[i] = result[i] / 2u
        }

        return BigUInt(result, base)
    }

    operator fun div(other: BigUInt): BigUInt {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")
        if (this == one || this == zero) return zero

        // Divide and conquer algorithm
        var left = zero
        var right = BigUInt(mag.copyOf(), base)
        var prevMid = zero

        while (true) {
            val mid = left + (right - left).divBy2()

            val productResult = other * mid

            // If result is the same.
            if (productResult == this || prevMid == mid) {
                return mid
            }
            if (productResult < this) {
                left = mid
            } else {
                right = mid
            }
            prevMid = mid
        }
    }

    fun pow(n: BigUInt): BigUInt {
        return when {
            n == zero -> one
            n % two == zero -> this.pow(n.divBy2()) * this.pow(n.divBy2())
            else -> this * this.pow(n.divBy2()) * this.pow(n.divBy2())
        }
    }

    fun pow(n: Int): BigUInt {
        return when {
            n == 0 -> one
            n % 2 == 0 -> this.pow(n / 2) * this.pow(n / 2)
            else -> this * this.pow(n / 2) * this.pow(n / 2)
        }
    }

    fun modPlus(other: BigUInt, m: BigUInt): BigUInt {
        val result = this + other
        return if (result > m) result - m else result
    }

    fun modMinus(other: BigUInt, m: BigUInt): BigUInt {
        val result = this - other
        return if (result > m) result - m else result
    }

    infix fun modInverse(other: BigUInt): BigUInt {
        val self = BigInteger(this.toString(), base.toInt())
        val otherBig = BigInteger(other.toString(), base.toInt())
        val result = self.modInverse(otherBig)

        return valueOf(result.toString(base.toInt()), base.toInt())
    }

    /**
     * This * n mod m using the Montgomery reduction algorithm.
     *
     * Beware that the number should be in the Montgomery form beforehand with the Montgomery transform.
     * e.g : ThisInMontgomery = This * r mod m, where r = base^k with r < base.pow(k)
     */
    fun montgomeryTimes(exponent: BigUInt, m: BigUInt, r: BigUInt): BigUInt {
        val v: BigUInt = m modInverse r

        val timeResult = this * exponent
        val modTimesResult = (timeResult * v) % r
        val higherPart = timeResult + modTimesResult * m
        val shiftResult = higherPart shl m.mag.size
        return if (shiftResult > m) shiftResult - m else shiftResult
    }

    fun modPow(exponent: BigUInt, m: BigUInt): BigUInt {
        if (base != exponent.base) throw NumberFormatException()
        if (base != m.base) throw NumberFormatException()

        // Convert to base 2
        val mBase2 = m.toBase(2u)
        val thisBase2 = this.toBase(2u)
        val r = two(base = 2u).pow(m.mag.size + 1)

        val thisMgy = thisBase2.montgomeryTimes(r.pow(2), mBase2, r)

        var p = r - mBase2
        for (i in mBase2.mag.size - 1 downTo 0) {
            p = p.montgomeryTimes(p, mBase2, r)
            if (mBase2.mag[i] == 1u) {
                p = p.montgomeryTimes(thisMgy, mBase2 , r)
            }
        }
        return p
    }

    operator fun rem(other: BigUInt): BigUInt {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")
        if (this == other || other == one) return zero

        val divResult = this / other
        val prodResult = other * divResult
        return this - prodResult
    }

    private fun UIntArray.stripTrailingZero(): UIntArray {
        // Find first nonzero byte
        var keep = this.size - 1
        while (keep > 0 && this[keep] == 0u) {
            keep--
        }
        return if (keep == this.size - 1) this else this.copyOfRange(0, keep + 1)
    }

    override fun toString(): String {
        return when (base) {
            in 1u..36u -> toStringBase(base)
            else -> mag.joinToString(
                prefix = "{",
                postfix = "}"
            )
        }
    }

    /**
     * Return a string number in specified base
     */
    private fun toStringBase(base: UInt): String {
        return mag.reversed().joinToString(separator = "") { it.toString(base.toInt()) }
    }

    override fun compareTo(other: BigUInt): Int {
        return when {
            this.mag.size < other.mag.size -> -1
            this.mag.size > other.mag.size -> 1
            else -> compareMagnitudeTo(other)
        }
    }

    private fun compareMagnitudeTo(other: BigUInt): Int {
        for (i in mag.size - 1 downTo 0) {
            if (mag[i] < other.mag[i]) {
                return -1
            } else if (mag[i] > other.mag[i]) {
                return 1
            }
        }
        return 0
    }

    fun toBase(newBase: UInt): BigUInt {
        if (base == newBase) return this

        val size = ceil((this.mag.size + 1) / log10(newBase.toDouble()) + 1).toInt()
        val result = UIntArray(size)
        val b = valueOf(newBase.toString(), base.toInt())

        var i = 0
        var num = this
        while (num != zero) {
            result[i] = (num % b).toStringBase(base).toUInt(base.toInt())
            num /= b

            i++
        }

        return BigUInt(result, newBase)
    }

    fun fromBase2toBase(newBase: UInt): BigUInt {
        if (newBase % 2u != 0u) throw ArithmeticException("newBase is not a multiple of 2")
        val thisBase2 = this.toBase(2u)
        val chunckSize = log2(newBase.toDouble()).toInt()
        val size = thisBase2.mag.size / chunckSize + 1
        val result = UIntArray(size)

        for (i in result.indices) {
            for (j in 0 until chunckSize) {
                result[i] += thisBase2.mag.elementAtOrElse(i * chunckSize + j) { 0u } shl j
            }
        }

        return BigUInt(result, newBase)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BigUInt

        if (this.compareTo(other) != 0) return false

        return true
    }
}
