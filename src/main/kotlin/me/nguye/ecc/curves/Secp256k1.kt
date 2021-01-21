package me.nguye.ecc.curves

import me.nguye.ecc.Curve
import me.nguye.number.BigUInt
import me.nguye.number.PointEcc

@ExperimentalUnsignedTypes
object Secp256k1 : Curve {
    /* Curves parameters */
    // y.pow(2) = x.pow(3) + ax + b
    override val a = BigUInt.valueOf("0000000000000000000000000000000000000000000000000000000000000000", radix = 16)
    override val b = BigUInt.valueOf("0000000000000000000000000000000000000000000000000000000000000007", radix = 16)

    /* Field parameters */
    // Prime number in which the curve operate in the finite field F_p
    // All points are taken modulo p.
    override val p = BigUInt.valueOf("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", radix = 16)

    // Order of the curve generator point G.
    override val n = BigUInt.valueOf("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", radix = 16)

    // Cofactor of the curve
    override val h = BigUInt.one

    // Generator Point
    override val generator
        get() = PointEcc(
            x = BigUInt.valueOf("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", radix = 16),
            y = BigUInt.valueOf("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", radix = 16),
            curve = Secp256k1
        )
}
