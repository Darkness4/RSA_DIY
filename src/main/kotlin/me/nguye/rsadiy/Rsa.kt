package me.nguye.rsadiy

import me.nguye.number.BigInt

@ExperimentalUnsignedTypes
object Rsa {
    fun decrypt(c: BigInt, d: BigInt, n: BigInt) = c.modPow(d, n)

    fun encrypt(m: BigInt, e: BigInt, n: BigInt) = m.modPow(e, n)
}