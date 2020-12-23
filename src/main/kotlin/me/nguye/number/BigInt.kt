package me.nguye.number

import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.pow

@ExperimentalUnsignedTypes
class BigInt(mag: UIntArray, val base: UInt, sign: Int = 1) : Comparable<BigInt> {
    companion object {
        private const val DEFAULT_BASE_STRING = 10

        /**
         * Store the [str] in the [BigInt] object with the [radix].
         */
        fun valueOf(str: String, radix: Int = DEFAULT_BASE_STRING): BigInt {
            var i = 0
            val (mag, sign) = if (str.first() == '-' || str.first() == '+') {
                i++
                UIntArray(str.length - 1) to -1
            } else {
                UIntArray(str.length) to 1
            }

            for (j in mag.size - 1 downTo 0) {
                mag[j] = Character.digit(str[i], radix).toUInt()
                i++
            }
            return BigInt(mag, radix.toUInt(), sign)
        }

        fun zero(base: UInt) = BigInt(UIntArray(1) { 0u }, base, 0)
        fun one(base: UInt) = BigInt(UIntArray(1) { 1u }, base, 1)
        fun basePowK(base: UInt, k: Int): BigInt {
            val mag = UIntArray(k + 1).apply {
                this[k] = 1u
            }
            return BigInt(mag, base, 1)
        }
    }

    private val zero by lazy { zero(base) }
    private val one by lazy { one(base) }
    private fun basePowK(k: Int): BigInt = basePowK(base, k)

    // Cache variable
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
        this.sign = if (this.mag.size == 1 && this.mag.first() == 0u) 0 else sign
    }

    operator fun unaryPlus() = this

    operator fun unaryMinus() = BigInt(mag, base, -sign)

    operator fun plus(other: BigInt): BigInt {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return other
        if (other == zero) return this
        if (this.sign != other.sign) {
            val cmp = this.compareUnsignedTo(other)
            if (cmp == 0) return zero
            val result = if (cmp > 0) this.subtractMagnitude(other) else other.subtractMagnitude(this)
            val resultSign = if (cmp == sign) 1 else -1
            return BigInt(result, base, resultSign)
        }

        val result = addMagnitude(other)

        return BigInt(result, base, sign)
    }

    private fun addMagnitude(other: BigInt): UIntArray {
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
        return result
    }

    operator fun minus(other: BigInt): BigInt {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return BigInt(other.mag, base, -other.sign)
        if (other == zero) return this
        if (this.sign != other.sign) {
            val result = this.addMagnitude(other)
            return BigInt(result, base, sign)
        }

        val result = subtractMagnitude(other)

        return if (this < other) BigInt(result, base, -1) else BigInt(result, base, 1)
    }

    private fun subtractMagnitude(other: BigInt): UIntArray {
        val result = UIntArray(max(mag.size, other.mag.size))
        var carry: UInt = 0u

        val (largest, smallest) = if (this.compareUnsignedTo(other) < 0) {
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
        return result
    }

    operator fun times(other: BigInt): BigInt {
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

        return BigInt(result, base, sign * other.sign)
    }

    /**
     * Shift the magnitude array to the left. (equivalent to dividing by base, since little endian)
     *
     * 0 are added on the right. No rotation.
     */
    infix fun shl(n: Int): BigInt {
        if (n == 0) return this

        // An example :
        // n = 2
        // mag = {0, 1, 2, 3, 4, 5, 6} (size = 7)
        // subArray = {2, 3, 4, 5, 6} (size = mag.size - n = 5)
        // zeroes = {0, 0, 0, 0, 0, 0, 0}
        // result = {2, 3, 4, 5, 6} (destinationOffset = n = 2)
        //
        // A critical case example (n >= mag.size: Shift all) :
        // n = 5
        // mag = {0, 1} (size = 2)
        // subArray = {}
        // zeroes = {0, 0}
        // result = {0, 0}
        val subArray = if (n < mag.size) mag.copyOfRange(n, mag.size) else UIntArray(0)

        val zeroes = UIntArray(mag.size)

        val result = subArray.copyInto(zeroes, 0, 0, subArray.size)
        return BigInt(result, base, sign)
    }

    fun divBy2(): BigInt {
        if (this == zero || this == one) return zero
        val result = mag.copyOf()

        var carry = 0u
        for (i in mag.size - 1 downTo 0) {
            result[i] = result[i] + carry
            carry = if (result[i] % 2u == 1u) base else 0u
            result[i] = result[i] shr 1
        }

        return BigInt(result, base, sign)
    }

    operator fun div(other: BigInt): BigInt {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")
        if (this == one || this == zero) return zero

        // Divide and conquer algorithm
        var left = zero
        var right = this
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

    operator fun rem(other: BigInt): BigInt {
        if (base != other.base) throw NumberFormatException()
        if (other == zero) throw ArithmeticException("/ by zero")
        if (this == other || other == one) return zero

        val divResult = this / other
        val prodResult = other * divResult
        return this - prodResult
    }

    /**
     * Return the remainder of `this mod base.pow(k)`.
     */
    infix fun remShl(k: Int): BigInt {
        if (k == 0) return zero

        val divResult = this shl k
        val prodResult = basePowK(k) * divResult
        return this - prodResult
    }

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

    fun modPow(exponent: BigInt, n: BigInt): BigInt {
        if (base != n.base) throw NumberFormatException()

        val exponentBase2 = exponent.toBase(2u)
        val r = basePowK(n.mag.size)
        val rSquare = basePowK(n.mag.size * 2) % n

        val v = r - (n modInverse r) // n * n' = 1 mod r, n' = "1/n mod r", v = r - n' = "-1/n mod r"

        val thisMgy = this.montgomeryTimes(rSquare, n, v)

        var p = r - n
        for (i in exponentBase2.mag.size - 1 downTo 0) {
            p = p.montgomeryTimes(p, n, v)
            if (exponentBase2.mag[i] == 1u) {
                p = p.montgomeryTimes(thisMgy, n, v)
            }
        }
        return p.montgomeryTimes(one, n, v)
    }

    override fun toString() = valueInString

    /**
     * Return a string number in specified base
     */
    private fun toStringBase(base: UInt): String {
        return mag.reversed().joinToString(separator = "") { it.toString(base.toInt()) }
    }

    fun toBase(newBase: UInt): BigInt {
        if (base > 36u) throw NumberFormatException("Base is not supported yet")  // TODO: Fix this
        if (newBase > base) throw NumberFormatException("Doesn't support base conversion to higher part.")
        if (base == newBase) return this

        val size = ceil((this.mag.size + 1) / log10(newBase.toDouble()) + 1).toInt()
        val result = UIntArray(size)
        val b = valueOf(newBase.toString(), base.toInt())  // TODO: Here, problem of base

        var i = 0
        var num = this
        while (num != zero) {
            result[i] = (num % b).toUInt()
            num /= b
            i++
        }

        return BigInt(result, newBase, sign)
    }

    fun fromBase2toBase(newBase: UInt): BigInt {
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

        return BigInt(result, newBase, sign)
    }

    fun toLong() = valueInLong

    fun toUInt() = valueInUInt

    override fun compareTo(other: BigInt): Int {
        return when {
            sign < other.sign -> -1 // This is negative, and other is positive
            sign > other.sign -> 1 // This is positive, and other is negative
            sign == -1 && other.sign == -1 -> -1 * this.compareUnsignedTo(other) // Both are negative.
            else -> this.compareUnsignedTo(other) // Both are positive
        }
    }

    private fun compareUnsignedTo(other: BigInt): Int {
        return when {
            this.mag.size < other.mag.size -> -1
            this.mag.size > other.mag.size -> 1
            else -> compareMagnitudeTo(other)
        }
    }

    private fun compareMagnitudeTo(other: BigInt): Int {
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

        other as BigInt

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
