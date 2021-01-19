package me.nguye.number

import me.nguye.ecc.Curve

@ExperimentalUnsignedTypes
data class PointEcc(val x: BigUInt, val y: BigUInt, val curve: Curve) {
    val zero by lazy { PointEcc(BigUInt.zero, BigUInt.zero, curve) }

    operator fun plus(other: PointEcc): PointEcc {
        // **WARNING: Every calculus is mod p and should be mod p!**
        if (this == zero) return other
        if (other == zero) return this

        // s = (p_y - q_y) / (p_x - q_x)
        val s = this.y.modSubtract(other.y, curve.p) * (
            this.x.modSubtract(
                other.x,
                curve.p
            ) modInverse curve.p
            )

        // r_x = s.pow(2) - p_x - q_x
        val resultX = (s * s).modSubtract(this.x, curve.p).modSubtract(other.x, curve.p)
        // r_y = s * (p_x - r_x) - p_y
        val resultY = (s * this.x.modSubtract(resultX, curve.p)).modSubtract(this.y, curve.p)

        return PointEcc(resultX, resultY, curve)
    }

    fun double(): PointEcc {
        // **WARNING: Every calculus is mod p and should be mod p!**
        if (this == zero) return zero

        // s = (3 * p_x.pow(2) + a) / (2 * p_y)
        val s = (BigUInt.three * this.x * this.x).modAdd(
            curve.a,
            curve.p
        ) * ((BigUInt.two * this.y) modInverse curve.p)

        // r_x = s.pow(2) - 2p_x
        val resultX = (s * s).modSubtract(this.x, curve.p).modSubtract(this.x, curve.p)
        // r_y = s * (p_x - r_x) - p_y
        val resultY = (s * this.x.modSubtract(resultX, curve.p)).modSubtract(this.y, curve.p)

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
