package me.nguye.ecc

import me.nguye.number.BigUInt
import me.nguye.number.PointEcc

@ExperimentalUnsignedTypes
interface Curve {
    /* Curves parameters */
    // y.pow(2) = x.pow(3) + ax + b mod p
    val a: BigUInt
    val b: BigUInt

    /* Field parameters */
    // Prime number in which the curve operate in the finite field F_p
    // All points are taken modulo p.
    val p: BigUInt

    // Order of the curve generator point G.
    val n: BigUInt

    // Cofactor of the curve
    val h: BigUInt

    // Generator Point
    val generator: PointEcc
}
