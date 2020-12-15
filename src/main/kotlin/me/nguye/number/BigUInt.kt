package me.nguye.number

import kotlin.math.max

@ExperimentalUnsignedTypes
class BigUByte(mag: UByteArray, val base: UByte = UByte.MAX_VALUE) : Comparable<BigUByte> {
    companion object {
        private const val DEFAULT_BASE_STRING = 10

        fun valueOf(str: String, radix: Int = DEFAULT_BASE_STRING): BigUByte {
            val mag = UByteArray(str.length)
            for ((i, char) in str.withIndex()) {
                mag[str.length - i - 1] = Character.digit(char, radix).toUByte()
            }
            return BigUByte(mag, radix.toUByte())
        }

        fun zero(base: UByte) = BigUByte(UByteArray(1) { 0u }, base)
        fun one(base: UByte) = BigUByte(UByteArray(1) { 1u }, base)
        fun two(base: UByte) = BigUByte(UByteArray(1) { 2u }, base)
    }

    private val zero by lazy { zero(base) }
    private val one by lazy { one(base) }
    private val two by lazy { two(base) }

    /**
     * The magnitude of this BigInteger, in <i>little-endian</i> order: the
     * zeroth element of this array is the least-significant int of the
     * magnitude.
     */
    private var mag: UByteArray

    init {
        this.mag = mag.stripTrailingZero()
    }

    operator fun plus(other: BigUByte): BigUByte {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return other
        if (other == zero) return this

        val result = UByteArray(max(mag.size, other.mag.size) + 1)
        var carry: UByte = 0u
        var i = 0

        // Add common parts of both numbers
        while (i < mag.size && i < other.mag.size) {
            val sum: UByte = mag[i] + other.mag[i] + carry
            result[i] = sum % base
            carry = sum / base
            i++
        }

        // Add the last part
        while (i < mag.size) {
            val sum: UByte = mag[i] + carry
            result[i] = sum % base
            carry = sum / base
            i++
        }
        while (i < other.mag.size) {
            val sum: UByte = other.mag[i] + carry
            result[i] = sum % other.base
            carry = sum / base
            i++
        }

        // Add the last carry (if exists)
        if (carry > 0uL) {
            result[i] = carry
        }

        return BigUByte(result, base)
    }

    operator fun minus(other: BigUByte): BigUByte {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return other
        if (other == zero) return this

        val result = UByteArray(max(mag.size, other.mag.size))
        var carry: UByte = 0u

        val (largest, smallest) = if (this < other) {
            other to this
        } else {
            this to other
        }

        // Subtract common parts of both numbers
        for (i in smallest.mag.indices) {
            var sub: UByte = largest.mag[i] - smallest.mag[i] - carry
            carry = if (largest.mag[i] < smallest.mag[i] + carry) {
                sub += largest.base
                1u
            } else 0u
            result[i] = sub
        }

        // Subtract the last part
        for (i in smallest.mag.size until largest.mag.size) {
            var sub: UByte = largest.mag[i] - carry
            carry = if (largest.mag[i] < carry) {
                sub += largest.base
                1u
            } else 0u
            result[i] = sub
        }

        return BigUByte(result, base)
    }

    operator fun times(other: BigUByte): BigUByte {
        if (base != other.base) throw NumberFormatException()
        if (this == zero || other == zero) return zero

        val result = UByteArray(mag.size + other.mag.size)

        // Basic multiplication
        for (i in other.mag.indices) {
            var carry: UByte = 0u
            for (j in mag.indices) {
                result[i + j] += other.mag[i] * mag[j] + carry
                carry = result[i + j] / base
                result[i + j] = result[i + j] % base
            }
            result[i + mag.size] = carry
        }

        return BigUByte(result, base)
    }

    fun divBy2(): BigUByte {
        if (this == zero || this == one) return zero
        val result = mag.copyOf()

        var carry: UByte = 0u
        for (i in mag.size - 1 downTo 0) {
            result[i] = result[i] + carry
            carry = if (result[i] % 2u == 1u) base else 0u
            result[i] = result[i] / 2u
        }

        return BigUByte(result, base)
    }

    operator fun div(other: BigUByte): BigUByte {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")

        // Divide and conquer algorithm (Newton - Raphson)
        var left = BigUByte(UByteArray(1) { 0u }, base)
        var right = BigUByte(mag.copyOf(), base)
        var prevMid = BigUByte(UByteArray(1) { 0u }, base)

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

    fun pow(n: BigUByte): BigUByte {
        return when {
            n == zero -> one
            n % two == zero -> this.pow(n.divBy2()) * this.pow(n.divBy2())
            else -> this * this.pow(n.divBy2()) * this.pow(n.divBy2())
        }
    }

    /**
     * this ^ n mod p
     */
    fun modPow(n: BigUByte, p: BigUByte): BigUByte {
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

    operator fun rem(other: BigUByte): BigUByte {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")
        if (this == other) return this
        if (other == one) return zero

        val divResult = this / other
        val prodResult = other * divResult
        return this - prodResult
    }

    private fun UByteArray.stripTrailingZero(): UByteArray {
        // Find first nonzero byte
        var keep = this.size - 1
        while (keep > 0 && this[keep] == 0u) {
            keep--
        }
        return if (keep == this.size - 1) this else this.copyOfRange(0, keep + 1)
    }

    override fun toString(): String {
        return toStringBase(base)
    }

    /**
     * Return a string number in specified base
     */
    private fun toStringBase(base: UByte): String {
        return mag.reversed().joinToString(separator = "") { it.toString(base.toInt()) }
    }

    override fun compareTo(other: BigUByte): Int {
        return when {
            this.mag.size < other.mag.size -> -1
            this.mag.size > other.mag.size -> 1
            else -> compareMagnitudeTo(other)
        }
    }

    fun compareMagnitudeTo(other: BigUByte): Int {
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

        other as BigUByte

        if (this.compareTo(other) != 0) return false

        return true
    }
}