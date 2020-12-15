package me.nguye.number

import kotlin.math.max

@ExperimentalUnsignedTypes
class BigUInt(mag: UIntArray, val base: UInt = UInt.MAX_VALUE) : Comparable<BigUInt> {
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

        // Divide and conquer algorithm
        var left = BigUInt(UIntArray(1) { 0u }, base)
        var right = BigUInt(mag.copyOf(), base)
        var prevMid = BigUInt(UIntArray(1) { 0u }, base)

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

    fun modInverse(other: BigUInt): BigUInt {
        if (other == zero) return zero
        var y = zero
        var x = one

        var a = this
        var m = other
        while (a > one) {
            // q is quotient
            val q = a / m
            var t = m

            // New remainder
            m = a % m
            a = t
            t = y

            // Update x and y
            y = x - q * y
            x = t
        }

        return x
    }


    fun modPlus(other: BigUInt, m: BigUInt): BigUInt {
        val result = this + other
        return if (result > m) result - m else result
    }

    fun modMinus(other: BigUInt, m: BigUInt): BigUInt {
        val result = this - other
        return if (result > m) result - m else result
    }

    /**
     * This ^ n mod p using the Montgomery reduction algorithm
     */
    fun montgomeryTimes(other: BigUInt, m: BigUInt): BigUInt {
        val baseBigUInt = BigUInt(uintArrayOf(base), base)
        val r = baseBigUInt.pow(m.mag.size + 1)
        val v: BigUInt = m.modInverse(r)

        val timeResult = this * other
        val modTimesResult = (timeResult * (r - v)) % r
        val higherPart = timeResult + modTimesResult * m
        val shiftResult = higherPart / r  // TODO: Since r is base^(size + 1), better use a shift
        return if (shiftResult > m) shiftResult - m else shiftResult
    }

    /**
     * this ^ n mod p
     */
    fun modPow(n: BigUInt, p: BigUInt): BigUInt {
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

    operator fun rem(other: BigUInt): BigUInt {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")
        if (this == other) return this
        if (other == one) return zero

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

    fun compareMagnitudeTo(other: BigUInt): Int {
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

        other as BigUInt

        if (this.compareTo(other) != 0) return false

        return true
    }
}
