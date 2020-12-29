package me.nguye.number

import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.pow

@ExperimentalUnsignedTypes
class BigInt(mag: UIntArray, val base: UInt, sign: Int = 1) : Comparable<BigInt> {
    companion object {
        private const val DEFAULT_BASE_STRING = 10
        private const val POSITIVE = 1
        private const val NEGATIVE = -1
        private const val NEUTRAL = 0

        /**
         * Store the [str] in the [BigInt] object with the [radix].
         *
         * E.g.: 1010 --> BigInt({0, 1, 0, 1}, base = 2, sign = 1)
         */
        fun valueOf(str: String, radix: Int = DEFAULT_BASE_STRING): BigInt {
            var i = 0
            val (mag, sign) = when {
                str.first() == '-' -> {
                    i++
                    UIntArray(str.length - 1) to NEGATIVE
                }
                str.first() == '+' -> {
                    i++
                    UIntArray(str.length - 1) to POSITIVE
                }
                else -> {
                    UIntArray(str.length) to POSITIVE
                }
            }

            for (j in mag.size - 1 downTo 0) {
                mag[j] = Character.digit(str[i], radix).toUInt()
                i++
            }
            return BigInt(mag, radix.toUInt(), sign)
        }

        fun zero(base: UInt) = BigInt(uintArrayOf(0u), base, sign = NEUTRAL)
        fun one(base: UInt) = BigInt(uintArrayOf(1u), base, sign = POSITIVE)

        /**
         * Returns base.pow(k).
         */
        fun basePowK(base: UInt, k: Int): BigInt {
            val mag = UIntArray(k + 1).apply {
                set(k, 1u)
            }
            return BigInt(mag, base, 1)
        }
    }

    private val zero by lazy { zero(base) }
    private val one by lazy { one(base) }
    private fun basePowK(k: Int): BigInt = basePowK(base, k)

    // Cache variables :
    // Assuming that BigInt is immutable, we can lazily cache the result of the conversions to avoid multiple
    // instantiation of the same number.
    //
    // See [by lazy](https://kotlinlang.org/docs/reference/delegated-properties.html#lazy)
    private val valueInLong by lazy {
        this.mag.foldIndexed(0L) { index, acc, value ->
            acc + base.toDouble().pow(index.toDouble()).toLong() * value.toLong()
        }
    }
    private val valueInUInt by lazy {
        this.mag.foldIndexed(0u) { index, acc, value ->
            acc + base.toDouble().pow(index.toDouble()).toUInt() * value
        }
    }
    private val valueInString by lazy {
        val builder = StringBuilder()
        if (this.sign == -1) builder.append('-')
        when (base) {
            in 1u..36u -> builder.append(toStringBase(base))
            else -> builder.append(
                this.mag.joinToString(
                    prefix = "{",
                    postfix = "}"
                )
            )
        }
        builder.toString()
    }

    /**
     * The magnitude of this BigInteger, in <i>little-endian</i> order: the
     * zeroth element of this array is the least-significant int of the
     * magnitude.
     */
    val mag: UIntArray

    /**
     * Sign of this Signed BigInteger.
     */
    val sign: Int

    init {
        this.mag = mag.stripTrailingZero()
        this.sign = if (this.mag.size == 1 && this.mag.first() == 0u) NEUTRAL else sign // Sign safety
    }

    operator fun unaryPlus() = this

    operator fun unaryMinus() = BigInt(mag, base, -sign)

    operator fun plus(other: BigInt): BigInt {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return other
        if (other == zero) return this

        if (this.sign != other.sign) {
            // Subtract instead
            val cmp = this.compareUnsignedTo(other)
            if (cmp == 0) return zero
            val result = if (cmp > 0) this subtractMagnitude other else other subtractMagnitude this
            val resultSign = if (cmp == sign) POSITIVE else NEGATIVE
            return BigInt(result, base, resultSign)
        }

        val result = this addMagnitude other

        return BigInt(result, base, sign)
    }

    /**
     * Unsigned Addition of two numbers.
     *
     * Returns only the magnitude array.
     */
    private infix fun addMagnitude(other: BigInt): UIntArray {
        val result = UIntArray(max(mag.size, other.mag.size) + 1)
        var carry = 0uL
        var i = 0

        // Add common parts of both numbers
        while (i < mag.size && i < other.mag.size) {
            val sum: ULong = mag[i] + other.mag[i] + carry
            result[i] = (sum % base).toUInt()
            carry = sum / base
            i++
        }

        // Add the last part
        while (i < mag.size) {
            val sum: ULong = mag[i] + carry
            result[i] = (sum % base).toUInt()
            carry = sum / base
            i++
        }
        while (i < other.mag.size) {
            val sum: ULong = other.mag[i] + carry
            result[i] = (sum % other.base).toUInt()
            carry = sum / base
            i++
        }

        // Add the last carry (if exists)
        if (carry > 0u) result[i] = carry.toUInt()
        return result
    }

    operator fun minus(other: BigInt): BigInt {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return BigInt(other.mag, base, -other.sign)
        if (other == zero) return this

        if (this.sign != other.sign) {
            // Add instead
            val result = this addMagnitude other
            return BigInt(result, base, sign)
        }

        val result = this subtractMagnitude other
        val resultSign = if (this < other) NEGATIVE else POSITIVE

        return BigInt(result, base, resultSign)
    }

    /**
     * Unsigned subtraction of two numbers.
     *
     * Returns only the magnitude array.
     */
    private infix fun subtractMagnitude(other: BigInt): UIntArray {
        val result = UIntArray(max(mag.size, other.mag.size))
        var carry = 0uL

        val (largest, smallest) = if (this.compareUnsignedTo(other) < 0) {
            other to this
        } else {
            this to other
        }

        // Subtract common parts of both numbers
        for (i in smallest.mag.indices) {
            var sub: ULong
            if (largest.mag[i] < carry + smallest.mag[i]) {
                sub = largest.mag[i] + (largest.base - carry - smallest.mag[i])
                carry = 1u
            } else {
                sub = largest.mag[i] - smallest.mag[i] - carry
                carry = 0u
            }
            result[i] = sub.toUInt()
        }

        // Subtract the last part
        for (i in smallest.mag.size until largest.mag.size) {
            var sub: ULong
            if (largest.mag[i] < carry) {
                sub = largest.mag[i] + (largest.base - carry)
                carry = 1u
            } else {
                sub = largest.mag[i] - carry
                carry = 0u
            }
            result[i] = sub.toUInt()
        }
        return result
    }

    operator fun times(other: BigInt): BigInt {
        if (base != other.base) throw NumberFormatException()
        if (this == zero || other == zero) return zero

        val result = UIntArray(mag.size + other.mag.size)

        // School case multiplication
        for (i in other.mag.indices) {
            var carry = 0uL
            for (j in mag.indices) {
                // Note: ULong is **necessary** to avoid overflow of other.mag[i] * mag[j].
                val sum: ULong = result[i + j].toULong() + other.mag[i].toULong() * mag[j].toULong() + carry
                carry = sum / base
                result[i + j] = (sum % base).toUInt()
            }
            result[i + mag.size] = carry.toUInt()
        }

        return BigInt(result, base, sign * other.sign)
    }

    /**
     * Shift the magnitude array to the left. (equivalent to dividing by base, since little endian)
     *
     * 0 are added on the right. No rotation.
     *
     * Algorithm description: Simple copy with the selected range.
     */
    infix fun shl(n: Int): BigInt {
        if (n == 0) return this
        val result = if (n < mag.size) mag.copyOfRange(n, mag.size) else uintArrayOf(0u)
        return BigInt(result, base, sign)
    }

    /**
     * Return the remainder of `this mod base.pow(k)`.
     *
     * Algorithm description: School case algorithm.
     * Remainder = a - b * Quotient.
     */
    infix fun remShl(k: Int): BigInt {
        if (k == 0) return zero

        val divResult = this shl k
        return this - basePowK(k) * divResult
    }

    /**
     * Return this / 2
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
    fun divBy2(): BigInt {
        if (this == zero || this == one) return zero
        val result = mag.copyOf()

        var carry = 0u
        for (i in mag.size - 1 downTo 0) {
            result[i] = result[i] + carry
            carry = if (result[i] % 2u == 1u) base else 0u // Store carry if remainder exist
            result[i] = result[i] shr 1 // Div by 2
        }

        return BigInt(result, base, sign)
    }

    /**
     * Returns this / [other]
     *
     * Algorithm Description: Binary Search Algorithm
     * We look for x, which solves [other] * x = this (or x = this / [other]).
     * Now, we know that f : x -> [other] * x is strictly monotonous and continuous.
     * Therefore, we can apply the Binary Search algorithm.
     */
    operator fun div(other: BigInt): BigInt {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")
        if (this == one || this == zero) return zero

        var left = zero
        var right = this
        var prevMid = zero

        while (true) {
            val mid = left + (right - left).divBy2()

            val productResult = other * mid

            when {
                productResult == this || prevMid == mid -> {  // Exit condition: mid = this / other.
                    return mid
                }
                productResult < this -> {  // mid < this / other. Too low.
                    left = mid  // x if after the middle.
                }
                else -> {  // mid > this / other. Too high.
                    right = mid  // x is before the middle.
                }
            }
            prevMid = mid
        }
    }

    /**
     * Remainder of this / [other].
     *
     * Algorithm Description: School case algorithm.
     * Remainder = a - b * quotient
     */
    operator fun rem(other: BigInt): BigInt {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")
        if (this == other || other == one) return zero

        val divResult = this / other
        return this - other * divResult
    }

    /**
     * Extended Euclidean Algorithm assuming this coprime [other].
     */
    infix fun modInverse(other: BigInt): BigInt {
        var (oldR, r) = this to other
        var (oldT, t) = one to zero

        if (other == one) return zero

        while (r > one) {
            val q = oldR / r

            (oldR - q * r).let {
                oldR = r
                r = it
            }

            (oldT - q * t).let {
                oldT = t
                t = it
            }
        }

        if (t < zero) t += other

        return t
    }

    /**
     * This * n mod m using the Montgomery reduction algorithm.
     *
     * Beware that the number should be in the Montgomery form beforehand with the Montgomery transform.
     * e.g : ThisInMontgomery = This * r mod n, where r = base^k with r < base.pow(k).
     */
    fun montgomeryTimes(other: BigInt, n: BigInt, v: BigInt): BigInt {
        val s = this * other
        val t = (s * v) remShl n.mag.size
        val m = s + t * n
        val u = m shl n.mag.size
        return if (u >= n) u - n else u
    }

    /**
     * this ^ exponent mod n using Montgomery Reduction Algorithm and Square-and-Multiply Algorithm.
     */
    fun modPow(exponent: BigInt, n: BigInt): BigInt {
        if (base != n.base) throw NumberFormatException()

        val exponentBase2 = exponent.toBase2()
        val r = basePowK(n.mag.size)
        val rSquare = basePowK(n.mag.size * 2) % n

        // v*n = -1 mod r = (r - 1) mod r
        val v = r - (n modInverse r)

        // Put this in montgomery form
        val thisMgy = this.montgomeryTimes(rSquare, n, v)

        var p = r - n // 1 in montgomery form
        for (i in exponentBase2.mag.size - 1 downTo 0) {
            p = p.montgomeryTimes(p, n, v) // Square : p = p*p
            if (exponentBase2.mag[i] == 1u) {
                p = p.montgomeryTimes(thisMgy, n, v) // Multiply : p = p * a
            }
        }

        // Return the result in the standard form
        return p.montgomeryTimes(one, n, v)
    }

    override fun toString() = valueInString

    /**
     * Return a string number in specified base
     */
    private fun toStringBase(base: UInt): String {
        return mag.reversed().joinToString(separator = "") { it.toString(base.toInt()) }
    }

    /**
     * Convert BigInt magnitude array to base 2.
     *
     * Algorithm description: Division method.
     */
    fun toBase2(): BigInt {
        if (base == 2u) return this
        val size = ceil((this.mag.size + 1) * log2(base.toDouble()) + 1).toInt()
        val result = UIntArray(size)

        var i = 0
        var num = this
        while (num != zero) {
            result[i] = num.mag[0] % 2u  // num % 2
            num = num.divBy2()
            i++
        }

        return BigInt(result, 2u, sign)
    }

    fun toBase2PowK(k: Int): BigInt {
        val newBase = 2.0.pow(k.toDouble()).toUInt()
        if (base == newBase) return this
        val thisBase2 = this.toBase2()
        val size = thisBase2.mag.size / k + 1
        val result = UIntArray(size)

        for (i in result.indices) {
            for (j in 0 until k) {
                result[i] += thisBase2.mag.elementAtOrElse(i * k + j) { 0u } shl j
            }
        }

        return BigInt(result, newBase, sign)
    }

    fun toLong() = valueInLong

    fun toUInt() = valueInUInt

    override fun compareTo(other: BigInt): Int {
        return when {
            sign < other.sign -> NEGATIVE // This is negative, and other is positive
            sign > other.sign -> POSITIVE // This is positive, and other is negative
            sign == NEGATIVE && other.sign == NEGATIVE -> -this.compareUnsignedTo(other) // Both are negative.
            else -> this.compareUnsignedTo(other) // Both are positive
        }
    }

    private fun compareUnsignedTo(other: BigInt): Int {
        return when {
            this.mag.size < other.mag.size -> NEGATIVE
            this.mag.size > other.mag.size -> POSITIVE
            else -> compareMagnitudeTo(other)
        }
    }

    private fun compareMagnitudeTo(other: BigInt): Int {
        // Check for the first biggest number
        for (i in mag.size - 1 downTo 0) {
            if (mag[i] < other.mag[i]) {
                return NEGATIVE
            } else if (mag[i] > other.mag[i]) {
                return POSITIVE
            }
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BigInt

        if (this.compareTo(other) != NEUTRAL) return false

        return true
    }

    private fun UIntArray.stripTrailingZero(): UIntArray {
        // Find first nonzero byte
        var keep = this.size - 1
        while (keep > 0 && this[keep] == 0u) {
            keep--
        }
        return if (keep == this.size - 1) this else this.copyOfRange(0, keep + 1)
    }
}
