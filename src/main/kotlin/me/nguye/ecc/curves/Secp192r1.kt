package me.nguye.ecc.curves

import me.nguye.ecc.Curve
import me.nguye.number.BigUInt
import me.nguye.number.PointEcc

@ExperimentalUnsignedTypes
object Secp192r1 : Curve {
    /* Curves parameters */
    // y.pow(2) = x.pow(3) + ax + b
    override val a = BigUInt.valueOf("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFC", radix = 16)
    override val b = BigUInt.valueOf("64210519E59C80E70FA7E9AB72243049FEB8DEECC146B9B1", radix = 16)

    /* Field parameters */
    // Prime number in which the curve operate in the finite field F_p
    // All points are taken modulo p.
    override val p = BigUInt.valueOf("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFF", radix = 16)

    // Order of the curve generator point G.
    override val n = BigUInt.valueOf("FFFFFFFFFFFFFFFFFFFFFFFF99DEF836146BC9B1B4D22831", radix = 16)

    // Cofactor of the curve
    override val h = BigUInt.one

    // Generator Point
    override val generator
        get() = PointEcc(
            BigUInt.valueOf("188DA80EB03090F67CBF20EB43A18800F4FF0AFD82FF1012", radix = 16),
            BigUInt.valueOf("07192B95FFC8DA78631011ED6B24CDD573F977A11E794811", radix = 16),
            curve = Secp192r1
        )
}
