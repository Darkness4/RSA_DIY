package me.nguye.number

import me.nguye.utils.divBy2
import me.nguye.utils.stripTrailingZero
import me.nguye.utils.toBase2Array
import me.nguye.utils.toBase2PowK
import kotlin.math.max

@ExperimentalUnsignedTypes
class BigUInt(mag: UIntArray) : Comparable<BigUInt> {
    companion object {
        private const val DEFAULT_BASE_STRING = 10
        private const val BASE = 2147483648u // 2.pow(31)
        private const val EXPONENT = 31

        /**
         * Store the [str] in the [BigUInt] object with the [radix].
         *
         * E.g.: 123 --> BigUInt({ 123 })
         */
        fun valueOf(str: String, radix: Int = DEFAULT_BASE_STRING): BigUInt {
            var i = 0
            val array = if (str.first() == '-' || str.first() == '+') {
                i++ // Skip the first
                UIntArray(str.length - 1)
            } else {
                UIntArray(str.length)
            }

            for (j in array.size - 1 downTo 0) {
                array[j] = Character.digit(str[i], radix).toUInt()
                i++
            }

            // Convert array in base `radix` to base 2^EXPONENT
            val mag = array.toBase2PowK(radix.toUInt(), EXPONENT)
            return BigUInt(mag)
        }

        val zero = BigUInt(uintArrayOf(0u))
        val one = BigUInt(uintArrayOf(1u))

        /**
         * Returns BASE.pow(k).
         */
        fun basePowK(k: Int): BigUInt {
            val mag = UIntArray(k + 1).apply {
                set(k, 1u)
            }
            return BigUInt(mag)
        }
    }

    /**
     * The magnitude of this BigInteger, in <i>little-endian</i> order: the
     * zeroth element of this array is the least-significant int of the
     * magnitude.
     */
    val mag: UIntArray = mag.stripTrailingZero()

    operator fun unaryPlus() = this

    operator fun unaryMinus() = this

    operator fun plus(other: BigUInt): BigUInt {
        if (this == zero) return other
        if (other == zero) return this

        val result = this addMagnitude other

        return BigUInt(result)
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
            carry = sum / BASE
            result[i] = (sum % BASE).toUInt()
            i++
        }

        // Add the last part
        while (i < mag.size) {
            val sum: ULong = mag[i] + carry
            carry = sum / BASE
            result[i] = (sum % BASE).toUInt() // sum % BASE
            i++
        }
        while (i < other.mag.size) {
            val sum: ULong = other.mag[i] + carry
            carry = sum / BASE
            result[i] = (sum % BASE).toUInt()
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
        return if (thisMod >= otherMod) thisMod - otherMod else m + thisMod - otherMod
    }

    operator fun minus(other: BigUInt): BigUInt {
        if (this == zero) return other
        if (other == zero) return this

        val result = this subtractMagnitude other

        return BigUInt(result)
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
                sub = largest.mag[i] + (BASE - carry - smallest.mag[i])
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
                sub = largest.mag[i] + (BASE - carry)
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
        if (this == zero || other == zero) return zero

        val result = UIntArray(mag.size + other.mag.size)

        // School case multiplication
        for (i in other.mag.indices) {
            var carry = 0uL
            for (j in mag.indices) {
                // Note: ULong is **necessary** to avoid overflow of other.mag[i] * mag[j].
                val sum: ULong = result[i + j].toULong() + other.mag[i].toULong() * mag[j].toULong() + carry
                carry = sum / BASE
                result[i + j] = (sum % BASE).toUInt()
            }
            result[i + mag.size] = carry.toUInt()
        }

        return BigUInt(result)
    }

    /**
     * Shift the magnitude array to the left. (equivalent to dividing by base, since little endian)
     *
     * 0 are added on the right. No rotation.
     *
     * Algorithm description: Simple copy with the selected range.
     */
    infix fun magShl(n: Int): BigUInt {
        if (n == 0) return this
        val result = if (n < mag.size) mag.copyOfRange(n, mag.size) else uintArrayOf(0u)
        return BigUInt(result)
    }

    /**
     * Return the remainder of `this / base.pow(k)`.
     *
     * Algorithm description: School case algorithm.
     * Remainder = a - b * Quotient.
     */
    infix fun remMagShl(k: Int): BigUInt {
        if (k == 0) return zero

        val divResult = this magShl k
        return this - basePowK(k) * divResult
    }

    /**
     * Return this / 2
     */
    private fun divBy2() = BigUInt(mag.divBy2(radix=BASE))

    /**
     * Returns this / [other]
     *
     * Algorithm Description: Binary Search Algorithm
     * We look for x, which solves [other] * x = this (or x = this / [other]).
     * Now, we know that f : x -> [other] * x is strictly monotonous and continuous.
     * Therefore, we can apply the Binary Search algorithm.
     */
    operator fun div(other: BigUInt): BigUInt {
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
        if (other == zero) throw ArithmeticException("/ by zero")
        if (this == other || other == one) return zero

        val divResult = this / other
        return this - other * divResult
    }

    /**
     * Extended Euclidean Algorithm assuming this coprime [other].
     */
    infix fun modInverse(other: BigUInt): BigUInt {
        if (other <= one) throw ArithmeticException("/ by zero")
        var (oldR, r) = this to other
        var (t, tIsNegative) = zero to false
        var (oldT, oldTIsNegative) = one to false

        while (oldR > one) {
            if (r == zero) throw ArithmeticException("/ by zero: not coprime with other")

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
        val t = (s * v) remMagShl n.mag.size // m % base.pow(n)
        val m = s + t * n
        val u = m magShl n.mag.size // m / base.pow(n)
        return if (u >= n) u - n else u
    }

    /**
     * this ^ exponent mod n using Montgomery Reduction Algorithm and Square-and-Multiply Algorithm.
     */
    fun modPow(exponent: BigUInt, n: BigUInt): BigUInt {
        val exponentBase2 = exponent.mag.toBase2Array(radix = BASE)
        val r = basePowK(n.mag.size)
        val rSquare = basePowK(n.mag.size * 2) % n

        // v*n = -1 mod r = (r - 1) mod r
        val v = r - (n modInverse r)

        // Put this in montgomery form
        val thisMgy = this.montgomeryTimes(rSquare, n, v)

        var p = r - n // 1 in montgomery form
        for (i in exponentBase2.size - 1 downTo 0) {
            p = p.montgomeryTimes(p, n, v) // Square : p = p*p
            if (exponentBase2[i] == 1u) {
                p = p.montgomeryTimes(thisMgy, n, v) // Multiply : p = p * a
            }
        }

        // Return the result in the standard form
        return p.montgomeryTimes(one, n, v)
    }

    override fun toString(): String {
        return this.mag.joinToString(
            prefix = "{",
            postfix = "}"
        )
    }

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
}
