package me.nguye.number

import kotlin.math.max

@ExperimentalUnsignedTypes
class BigULong(mag: ULongArray, val base: ULong = ULong.MAX_VALUE) : Comparable<BigULong> {
    companion object {
        private const val DEFAULT_BASE_STRING = 10

        fun valueOf(str: String, radix: Int = DEFAULT_BASE_STRING): BigULong {
            val mag = ULongArray(str.length)
            for ((i, char) in str.withIndex()) {
                mag[str.length - i - 1] = Character.digit(char, radix).toULong()
            }
            return BigULong(mag, radix.toULong())
        }

        fun zero(base: ULong) = BigULong(ULongArray(1) { 0uL }, base)
        fun one(base: ULong) = BigULong(ULongArray(1) { 1uL }, base)
        fun two(base: ULong) = BigULong(ULongArray(1) { 2uL }, base)
    }

    private val zero by lazy { zero(base) }
    private val one by lazy { one(base) }
    private val two by lazy { two(base) }

    /**
     * The magnitude of this BigInteger, in <i>little-endian</i> order: the
     * zeroth element of this array is the least-significant int of the
     * magnitude.
     */
    private var mag: ULongArray

    init {
        this.mag = mag.stripTrailingZero()
    }

    operator fun plus(other: BigULong): BigULong {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return other
        if (other == zero) return this

        val result = ULongArray(max(mag.size, other.mag.size) + 1)
        var carry = 0uL
        var i = 0

        // Add common parts of both numbers
        while (i < mag.size && i < other.mag.size) {
            val sum = mag[i] + other.mag[i] + carry
            result[i] = sum % base
            carry = sum / base
            i++
        }

        // Add the last part
        while (i < mag.size) {
            val sum = mag[i] + carry
            result[i] = sum % base
            carry = sum / base
            i++
        }
        while (i < other.mag.size) {
            val sum = other.mag[i] + carry
            result[i] = sum % other.base
            carry = sum / base
            i++
        }

        // Add the last carry (if exists)
        if (carry > 0uL) {
            result[i] = carry
        }

        return BigULong(result, base)
    }

    operator fun minus(other: BigULong): BigULong {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return other
        if (other == zero) return this

        val result = ULongArray(max(mag.size, other.mag.size))
        var carry = 0uL

        val (largest, smallest) = if (this < other) {
            other to this
        } else {
            this to other
        }

        // Subtract common parts of both numbers
        for (i in smallest.mag.indices) {
            var sub = largest.mag[i] - smallest.mag[i] - carry
            carry = if (largest.mag[i] < smallest.mag[i] + carry) {
                sub += largest.base
                1uL
            } else 0uL
            result[i] = sub
        }

        // Subtract the last part
        for (i in smallest.mag.size until largest.mag.size) {
            var sub = largest.mag[i] - carry
            carry = if (largest.mag[i] < carry) {
                sub += largest.base
                1uL
            } else 0uL
            result[i] = sub
        }

        return BigULong(result, base)
    }

    operator fun times(other: BigULong): BigULong {
        if (base != other.base) throw NumberFormatException()
        if (this == zero || other == zero) return zero

        val result = ULongArray(mag.size + other.mag.size)

        // Basic multiplication
        for (i in other.mag.indices) {
            var carry = 0uL
            for (j in mag.indices) {
                result[i + j] += other.mag[i] * mag[j] + carry
                carry = result[i + j] / base
                result[i + j] = result[i + j] % base
            }
            result[i + mag.size] = carry
        }

        return BigULong(result, base)
    }

    fun divBy2(): BigULong {
        if (this == zero || this == one) return zero
        val result = mag.copyOf()

        var carry = 0uL
        for (i in mag.size - 1 downTo 0) {
            result[i] = result[i] + carry
            carry = if (result[i] % 2u == 1uL) base else 0u
            result[i] = result[i] / 2u
        }

        return BigULong(result, base)
    }

    operator fun div(other: BigULong): BigULong {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")

        // Divide and conquer algorithm (Newton - Raphson)
        var left = BigULong(ULongArray(1) { 0u }, base)
        var right = BigULong(mag.copyOf(), base)
        var prevMid = BigULong(ULongArray(1) { 0u }, base)

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

    fun pow(n: BigULong): BigULong {
        return when {
            n == zero -> one
            n % two == zero -> this.pow(n.divBy2()) * this.pow(n.divBy2())
            else -> this * this.pow(n.divBy2()) * this.pow(n.divBy2())
        }
    }

    /**
     * this ^ n mod p
     */
    fun modPow(n: BigULong, p: BigULong): BigULong {
        if (p == one) return zero
        var base = this % p
        var exponent = n
        var result = one
        while (exponent > zero) {
            if (exponent % two == one) {
                result = (result * base) % p
            }
            exponent = exponent.divBy2()
            base = (base * base) % p
        }
        return result
    }

    operator fun rem(other: BigULong): BigULong {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")
        if (this == other) return this
        if (other == one) return zero

        val divResult = this / other
        val prodResult = other * divResult
        return this - prodResult
    }

    private fun ULongArray.stripTrailingZero(): ULongArray {
        // Find first nonzero byte
        var keep = this.size - 1
        while (keep > 0 && this[keep] == 0uL) {
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
    private fun toStringBase(base: ULong): String {
        return mag.reversed().joinToString(separator = "") { it.toString(base.toInt()) }
    }

    override fun compareTo(other: BigULong): Int {
        return when {
            this.mag.size < other.mag.size -> -1
            this.mag.size > other.mag.size -> 1
            else -> compareMagnitudeTo(other)
        }
    }

    fun compareMagnitudeTo(other: BigULong): Int {
        for (i in mag.size - 1 downTo 0) {
            if (mag[i] < other.mag[i]) {
                return -1
            } else if (mag[i] > other.mag[i]) {
                return 1
            }
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BigULong

        if (this.compareTo(other) != 0) return false

        return true
    }
}
