package me.nguye.rsadiy

import me.nguye.ecc.Curve
import me.nguye.number.BigUInt
import me.nguye.number.PointEcc

@ExperimentalUnsignedTypes
class Edch(private val curve: Curve) {
    fun generateKey(): BigUInt = BigUInt.randomBelow(curve.n)

    fun calculateSharedKey(theirPubKey: PointEcc, yourPrivateKey: BigUInt): PointEcc {
        return theirPubKey.scalarMultiply(yourPrivateKey)
    }

    fun fakeKeyExchange(): Boolean {
        val alicePrivateKey = generateKey()
        val alicePublicKey = curve.generator * alicePrivateKey
        println("Alice private key: $alicePrivateKey")
        println("Alice public key: $alicePublicKey")

        val bobPrivateKey = generateKey()
        val bobPublicKey = curve.generator * bobPrivateKey
        println("Bob private key: $bobPrivateKey")
        println("Bob public key: $bobPublicKey")

        println("Key Exchange !!!")

        val aliceSharedKey = bobPublicKey * alicePrivateKey
        println("Alice shared key: $aliceSharedKey")

        val bobSharedKey = alicePublicKey * bobPrivateKey
        println("Bob shared key: $bobSharedKey")

        println("Equality : ${aliceSharedKey == bobSharedKey}")
        return aliceSharedKey == bobSharedKey
    }
}
