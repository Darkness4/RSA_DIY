package me.nguye.rsadiy

import me.nguye.number.BigUInt

@ExperimentalUnsignedTypes
object Rsa {
    fun decrypt(c: BigUInt, d: BigUInt, n: BigUInt) = c.modPow(d, n)

    fun encrypt(m: BigUInt, e: BigUInt, n: BigUInt) = m.modPow(e, n)
}
