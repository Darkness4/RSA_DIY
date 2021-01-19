package me.nguye.ecc.curves

import me.nguye.ecc.Curve
import me.nguye.number.BigUInt
import me.nguye.number.PointEcc

@ExperimentalUnsignedTypes
object Secp521r1 : Curve {
    /* Curves parameters */
    // y.pow(2) = x.pow(3) + ax + b
    override val a = BigUInt.valueOf(
        "01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC",
        radix = 16
    )
    override val b = BigUInt.valueOf(
        "0051953EB9618E1C9A1F929A21A0B68540EEA2DA725B99B315F3B8B489918EF109E156193951EC7E937B1652C0BD3BB1BF073573DF883D2C34F1EF451FD46B503F00",
        radix = 16
    )

    /* Field parameters */
    // Prime number in which the curve operate in the finite field Fp
    // All points are taken modulo p.
    override val p = BigUInt.valueOf(
        "01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
        radix = 16
    )

    // Order of the curve generator point G.
    override val n = BigUInt.valueOf(
        "01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA51868783BF2F966B7FCC0148F709A5D03BB5C9B8899C47AEBB6FB71E91386409",
        radix = 16
    )

    // Cofactor of the curve
    override val h = BigUInt.one

    // Generator Point
    override val generator
        get() = PointEcc(
            BigUInt.valueOf("00C6858E06B70404E9CD9E3ECB662395B4429C648139053FB521F828AF606B4D3DBAA14B5E77EFE75928FE1DC127A2FFA8DE3348B3C1856A429BF97E7E31C2E5BD66"),
            BigUInt.valueOf("011839296A789A3BC0045C8A5FB42C7D1BD998F54449579B446817AFBD17273E662C97EE72995EF42640C550B9013FAD0761353C7086A272C24088BE94769FD16650"),
            curve = Secp521r1
        )
}
