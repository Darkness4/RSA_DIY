package me.nguye.number

import java.math.BigInteger
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalUnsignedTypes
class BigInt(mag: UIntArray, val base: UInt, val sign: Int) : Comparable<BigInt> {
    companion object {
        private const val DEFAULT_BASE_STRING = 10

        /**
         * Store the [str] in the [BigInt] object with the [radix].
         */
        fun valueOf(str: String, radix: Int = DEFAULT_BASE_STRING): BigInt {
            var i = 0
            val (mag, sign) = if (str.first() == '-') {
                i++
                UIntArray(str.length - 1) to -1
            } else {
                UIntArray(str.length) to 1
            }

            for (j in mag.size - 1 downTo 0) {
                mag[j] = Character.digit(str[i], radix).toUInt()
                i++
            }
            val result = BigInt(mag, radix.toUInt(), sign)
            val zero = zero(radix.toUInt())
            if (result.compareUnsignedTo(zero) == 0) return zero
            return BigInt(mag, radix.toUInt(), sign)
        }

        fun zero(base: UInt) = BigInt(UIntArray(1) { 0u }, base, 0)
        fun one(base: UInt) = BigInt(UIntArray(1) { 1u }, base, 1)
        fun twoPowK(k: Int) = basePowK(2u, k)
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

    /**
     * The magnitude of this BigInteger, in <i>little-endian</i> order: the
     * zeroth element of this array is the least-significant int of the
     * magnitude.
     */
    var mag: UIntArray

    init {
        this.mag = mag.stripTrailingZero()
    }

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

    operator fun minus(other: BigInt): BigInt {
        if (base != other.base) throw NumberFormatException()
        if (this == zero) return BigInt(other.mag, base, -other.sign)
        if (other == zero) return this
        if (this.sign != other.sign) {
            val result = this.addMagnitude(other)
            return BigInt(result, base, sign)
        }

        val result = subtractMagnitude(other)

        return if (this < other) {
            BigInt(result, base, -1)
        } else {
            BigInt(result, base, 1)
        }
    }

    operator fun unaryMinus() = BigInt(mag, base, -sign)

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
        val subArray = mag.copyOfRange(n, mag.size)

        val zeroes = UIntArray(mag.size)

        val result = subArray.copyInto(zeroes, 0, 0, subArray.size)
        return BigInt(result, base, sign)
    }

    fun divBy2(): BigInt {
        if (this == zero || this == one) return zero
        val result = mag.copyOf()

        var carry: UInt = 0u
        for (i in mag.size - 1 downTo 0) {
            result[i] = result[i] + carry
            carry = if (result[i] % 2u == 1u) base else 0u
            result[i] = result[i] / 2u
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

        val basePowK = basePowK(k)
        val divResult = this shl k
        val prodResult = basePowK * divResult
        return this - prodResult
    }

    infix fun modInverse(other: BigInt): BigInt {
        val self = BigInteger(this.toString(), base.toInt())
        val otherBig = BigInteger(other.toString(), base.toInt())
        val result = self.modInverse(otherBig)

        return valueOf(result.toString(base.toInt()), base.toInt())
    }

    /**
     * This * n mod m using the Montgomery reduction algorithm.
     *
     * Beware that the number should be in the Montgomery form beforehand with the Montgomery transform.
     * e.g : ThisInMontgomery = This * r mod n, where r = base^k with r < base.pow(k)
     */
    fun montgomeryTimes(other: BigInt, n: BigInt, v: BigInt): BigInt {
        val timesResult = this * other
        val negativeLowerPart = (timesResult * v) remShl n.mag.size
        val higherPart = timesResult + negativeLowerPart * n  // TODO: Use MulAdd here
        val shiftResult = higherPart shl n.mag.size
        return shiftResult % n  // TODO: Optimize here !
    }

    @ExperimentalTime
    fun modPow(exponent: BigInt, n: BigInt): BigInt {
        if (base != exponent.base) throw NumberFormatException()
        if (base != n.base) throw NumberFormatException()

        val exponentBase2 = exponent.toBase(2u)
        val r = basePowK(n.mag.size)
        val rSquare = basePowK(n.mag.size * 2)

        println("Calculating v...")
        val v = -(n modInverse r)  // r * r' - n * v = 1, n * v = -1 mod r, "v = -1/n mod r"
        println("v=$v")

        println("Calculating Montgomery Form...")
        val thisMgy = this.montgomeryTimes(rSquare, n, v)
        println("phi(a)=$thisMgy")

        var pMgy = r - n
        var oldTime = Duration.ZERO
        for (i in exponentBase2.mag.size - 1 downTo 0) {
            val time = measureTime {
                pMgy = pMgy.montgomeryTimes(pMgy, n, v)
                if (exponentBase2.mag[i] == 1u) {
                    pMgy = pMgy.montgomeryTimes(thisMgy, n, v)
                }
            }
            println("${i}/${exponentBase2.mag.size} left")
            println("Time Measured: $time. ETA: ${(oldTime+time)/2 * i}")
            oldTime = time
        }
        return pMgy.montgomeryTimes(one , n, v)
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

    private fun UIntArray.stripTrailingZero(): UIntArray {
        // Find first nonzero byte
        var keep = this.size - 1
        while (keep > 0 && this[keep] == 0u) {
            keep--
        }
        return if (keep == this.size - 1) this else this.copyOfRange(0, keep + 1)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        if (sign == -1) builder.append('-')
        when (base) {
            in 1u..36u -> builder.append(toStringBase(base))
            else -> builder.append(
                mag.joinToString(
                    prefix = "{",
                    postfix = "}"
                )
            )
        }
        return builder.toString()
    }

    /**
     * Return a string number in specified base
     */
    private fun toStringBase(base: UInt): String {
        return mag.reversed().joinToString(separator = "") { it.toString(base.toInt()) }
    }

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

    fun toBase(newBase: UInt): BigInt {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BigInt

        if (this.compareTo(other) != 0) return false

        return true
    }
}
