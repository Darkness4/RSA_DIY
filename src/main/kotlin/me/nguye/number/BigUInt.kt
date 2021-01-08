package me.nguye.number

import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.pow

@ExperimentalUnsignedTypes
class BigUInt(mag: UIntArray, val base: UInt) : Comparable<BigUInt> {
    companion object {
        private const val DEFAULT_BASE_STRING = 10

        /**
         * Store the [str] in the [BigUInt] object with the [radix].
         *
         * E.g.: 1010 --> BigInt({0, 1, 0, 1}, base = 2, sign = 1)
         */
        fun valueOf(str: String, radix: Int = DEFAULT_BASE_STRING): BigUInt {
            var i = 0
            val mag = when {
                str.first() == '-' || str.first() == '+' -> {
                    i++
                    UIntArray(str.length - 1)
                }
                else -> {
                    UIntArray(str.length)
                }
            }

            for (j in mag.size - 1 downTo 0) {
                mag[j] = Character.digit(str[i], radix).toUInt()
                i++
            }
            return BigUInt(mag, radix.toUInt())
        }

        fun zero(base: UInt) = BigUInt(uintArrayOf(0u), base)
        fun one(base: UInt) = BigUInt(uintArrayOf(1u), base)

        /**
         * Returns base.pow(k).
         */
        fun basePowK(base: UInt, k: Int): BigUInt {
            val mag = UIntArray(k + 1).apply {
                set(k, 1u)
            }
            return BigUInt(mag, base)
        }
    }

    private val zero by lazy { zero(base) }
    private val one by lazy { one(base) }
    private fun basePowK(k: Int): BigUInt = basePowK(base, k)

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
    private val valueInString by lazy {
        val builder = StringBuilder()
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

    init {
        this.mag = mag.stripTrailingZero()
    }

    operator fun unaryPlus() = this

    operator fun unaryMinus() = this

    operator fun plus(other: BigUInt): BigUInt {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return other
        if (other == zero) return this

        val result = this addMagnitude other

        return BigUInt(result, base)
    }

    /**
     * Unsigned Addition of two numbers.
     *
     * Returns only the magnitude array.
     */
    private infix fun addMagnitude(other: BigUInt): UIntArray {
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

    fun modAdd(other: BigUInt, m: BigUInt): BigUInt {
        val thisMod = if (this >= m || this < zero) this % m else this
        val otherMod = if (other >= m || other < zero) other % m else other
        val result = thisMod + otherMod
        return if (result >= m) result - m else result
    }

    fun modMinus(other: BigUInt, m: BigUInt): BigUInt {
        val thisMod = if (this >= m || this < zero) this % m else this
        val otherMod = if (other >= m || other < zero) other % m else other
        val result = thisMod - otherMod
        return if (result < zero) result + m else result
    }

    operator fun minus(other: BigUInt): BigUInt {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return other
        if (other == zero) return this

        val result = this subtractMagnitude other

        return BigUInt(result, base)
    }

    /**
     * Unsigned subtraction of two numbers.
     *
     * Returns only the magnitude array.
     */
    private infix fun subtractMagnitude(other: BigUInt): UIntArray {
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

    operator fun times(other: BigUInt): BigUInt {
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

        return BigUInt(result, base)
    }

    /**
     * Shift the magnitude array to the left. (equivalent to dividing by base, since little endian)
     *
     * 0 are added on the right. No rotation.
     *
     * Algorithm description: Simple copy with the selected range.
     */
    infix fun shl(n: Int): BigUInt {
        if (n == 0) return this
        val result = if (n < mag.size) mag.copyOfRange(n, mag.size) else uintArrayOf(0u)
        return BigUInt(result, base)
    }

    /**
     * Return the remainder of `this mod base.pow(k)`.
     *
     * Algorithm description: School case algorithm.
     * Remainder = a - b * Quotient.
     */
    infix fun remShl(k: Int): BigUInt {
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
    fun divBy2(): BigUInt {
        if (this == zero || this == one) return zero
        val result = mag.copyOf()

        var carry = 0u
        for (i in mag.size - 1 downTo 0) {
            result[i] = result[i] + carry
            carry = if (result[i] % 2u == 1u) base else 0u // Store carry if remainder exist
            result[i] = result[i] shr 1 // Div by 2
        }

        return BigUInt(result, base)
    }

    /**
     * Returns this / [other]
     *
     * Algorithm Description: Binary Search Algorithm
     * We look for x, which solves [other] * x = this (or x = this / [other]).
     * Now, we know that f : x -> [other] * x is strictly monotonous and continuous.
     * Therefore, we can apply the Binary Search algorithm.
     */
    operator fun div(other: BigUInt): BigUInt {
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
                productResult == this || prevMid == mid -> { // Exit condition: mid = this / other.
                    return mid
                }
                productResult < this -> { // mid < this / other. Too low.
                    left = mid // x if after the middle.
                }
                else -> { // mid > this / other. Too high.
                    right = mid // x is before the middle.
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
    operator fun rem(other: BigUInt): BigUInt {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")
        if (this == other || other == one) return zero

        val divResult = this / other
        return this - other * divResult
    }

    /**
     * Extended Euclidean Algorithm assuming this coprime [other].
     */
    infix fun modInverse(other: BigUInt): BigUInt {
        if (other <= one) return zero
        var (oldR, r) = this to other
        var (t, tIsNegative) = zero to false
        var (oldT, oldTIsNegative) = one to false

        while (oldR > one) {
            if (r == zero) // this and other are not coprime
                return zero

            val q = oldR / r

            // (r, oldR) = (oldR - q * r, r)
            (oldR - q * r).let {
                oldR = r
                r = it
            }

            // (t, oldT) = (oldT - q * t, t)
            val qt = q * t
            val tempT = t
            val tempTIsNegative = tIsNegative
            if (tIsNegative == oldTIsNegative) {
                if (oldT > qt) { // oldT - q * t >= 0. Default case.
                    t = oldT - qt
                    tIsNegative = oldTIsNegative
                } else { // oldT - q * t < 0. We swap the members.
                    t = qt - oldT
                    tIsNegative = !tIsNegative // Switch the sign because oldT - q * t < 0
                }
            } else { // oldT and t don't have the same sign. The subtraction become an addition.
                t = oldT + qt
                tIsNegative = oldTIsNegative
            }
            oldT = tempT
            oldTIsNegative = tempTIsNegative
        }

        return if (oldTIsNegative) (other - oldT) else oldT
    }

    /**
     * This * n mod m using the Montgomery reduction algorithm.
     *
     * Beware that the number should be in the Montgomery form beforehand with the Montgomery transform.
     * e.g : ThisInMontgomery = This * r mod n, where r = base^k with r < base.pow(k).
     */
    fun montgomeryTimes(other: BigUInt, n: BigUInt, v: BigUInt): BigUInt {
        val s = this * other
        val t = (s * v) remShl n.mag.size  // m % base.pow(n)
        val m = s + t * n
        val u = m shl n.mag.size  // m / base.pow(n)
        return if (u >= n) u - n else u
    }

    /**
     * this ^ exponent mod n using Montgomery Reduction Algorithm and Square-and-Multiply Algorithm.
     */
    fun modPow(exponent: BigUInt, n: BigUInt): BigUInt {
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
    fun toBase2(): BigUInt {
        if (base == 2u) return this
        val size = ceil(this.mag.size * log2(base.toDouble())).toInt()
        val result = UIntArray(size)

        var i = 0
        var num = this
        while (num != zero) {
            result[i] = num.mag[0] % 2u // num % 2
            num = num.divBy2()
            i++
        }

        return BigUInt(result, 2u)
    }

    fun toBase2PowK(k: Int): BigUInt {
        val newBase = 1u shl k // 2.pow(k)
        if (base == newBase) return this
        val thisBase2 = this.toBase2()
        val result = UIntArray(size = thisBase2.mag.size / k + 1)

        for (chunkIndex in result.indices) {
            for (offset in 0 until k) { // k = chunckSize
                // result[chunkIndex] += x * 2.pow(offset)
                // x is a bit. x = thisBase2.mag[chunkIndex * k + offset]
                // If thisBase2.mag[chunkIndex * k + offset] fails, it returns 0u.
                result[chunkIndex] += thisBase2.mag.elementAtOrElse(chunkIndex * k + offset) { 0u } shl offset
            }
        }

        return BigUInt(result, newBase)
    }

    fun toLong() = valueInLong

    override fun compareTo(other: BigUInt): Int {
        return this.compareUnsignedTo(other)
    }

    private fun compareUnsignedTo(other: BigUInt): Int {
        return when {
            this.mag.size < other.mag.size -> -1
            this.mag.size > other.mag.size -> 1
            else -> compareMagnitudeTo(other)
        }
    }

    private fun compareMagnitudeTo(other: BigUInt): Int {
        // Check for the first biggest number
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

    private fun UIntArray.stripTrailingZero(): UIntArray {
        // Find first nonzero byte
        var keep = this.size - 1
        while (keep > 0 && this[keep] == 0u) {
            keep--
        }
        return if (keep == this.size - 1) this else this.copyOfRange(0, keep + 1)
    }
}
