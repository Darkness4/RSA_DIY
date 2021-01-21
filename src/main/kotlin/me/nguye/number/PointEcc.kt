package me.nguye.number

import me.nguye.ecc.Curve

@ExperimentalUnsignedTypes
data class PointEcc(val x: BigUInt, val y: BigUInt, val curve: Curve) {
    val zero by lazy { PointEcc(BigUInt.zero, BigUInt.zero, curve) }

    // **WARNING: Every calculus is mod p and should be mod p!**
    private infix fun BigUInt.modTimes(other: BigUInt) = this.modTimes(other, curve.p)
    private infix fun BigUInt.modSubtract(other: BigUInt) = this.modSubtract(other, curve.p)
    private infix fun BigUInt.modAdd(other: BigUInt) = this.modAdd(other, curve.p)
    private fun BigUInt.modInverse() = this modInverse curve.p

    operator fun plus(other: PointEcc): PointEcc {
        if (this == zero) return other
        if (other == zero) return this

        // s = (p_y - q_y) / (p_x - q_x)
        val s = (this.y modSubtract other.y) modTimes
                ((this.x modSubtract other.x).modInverse())

        // r_x = s.pow(2) - p_x - q_x
        val resultX = (s modTimes s) modSubtract this.x modSubtract other.x
        // r_y = s * (p_x - r_x) - p_y
        val resultY = s modTimes (this.x modSubtract resultX) modSubtract this.y

        return PointEcc(resultX, resultY, curve)
    }

    fun double(): PointEcc {
        if (this == zero) return zero

        // s = (3 * p_x.pow(2) + a) / (2 * p_y)
        val s = ((BigUInt.three modTimes this.x modTimes this.x) modAdd curve.a) modTimes
                ((BigUInt.two modTimes this.y).modInverse())

        // r_x = s.pow(2) - 2p_x
        val resultX = (s modTimes s) modSubtract (BigUInt.two modTimes this.x)
        // r_y = s * (p_x - r_x) - p_y
        val resultY = s modTimes (this.x modSubtract resultX) modSubtract this.y

        return PointEcc(resultX, resultY, curve)
    }

    operator fun times(other: BigUInt) = this scalarMultiply other

    infix fun scalarMultiply(k: BigUInt): PointEcc {
        val kBase2 = k.toBase2Array()
        var n = this
        var r = zero
        for (bit in kBase2) {
            if (bit == 1u) r += n
            n = n.double()
        }
        return r
    }
}
