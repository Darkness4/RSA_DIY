package me.nguye.rsadiy

import me.nguye.ecc.Curve
import me.nguye.number.BigUInt
import me.nguye.number.PointEcc
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalUnsignedTypes
class Edch(private val curve: Curve) {
    fun generatePrivateKey(): BigUInt = BigUInt.randomBelow(curve.n)

    fun generatePublicKey(privateKey: BigUInt): PointEcc = curve.generator * privateKey

    fun generateSharedKey(theirPublicKey: PointEcc, yourPrivateKey: BigUInt): PointEcc = theirPublicKey * yourPrivateKey

    @ExperimentalTime
    fun fakeKeyExchange(): Boolean {
        val alicePrivateKey = generatePrivateKey()
        var alicePublicKey: PointEcc
        measureTime {
            alicePublicKey = generatePublicKey(alicePrivateKey)
        }.also {
            println("Alice public key : $it")
            println("Alice private key: $alicePrivateKey")
            println("Alice public key: $alicePublicKey")
        }

        val bobPrivateKey = generatePrivateKey()
        var bobPublicKey: PointEcc
        measureTime {
            bobPublicKey = generatePublicKey(bobPrivateKey)
        }.also {
            println("Bob public key : $it")
            println("Bob private key: $bobPrivateKey")
            println("Bob public key: $bobPublicKey")
        }

        println("Key Exchange !!!")

        val aliceSharedKey: PointEcc
        measureTime {
            aliceSharedKey = generateSharedKey(bobPublicKey, alicePrivateKey)
        }.also {
            println("Alice shared key : $it")
            println("Alice shared key: $aliceSharedKey")
        }

        val bobSharedKey: PointEcc
        measureTime {
            bobSharedKey = generateSharedKey(alicePublicKey, bobPrivateKey)
        }.also {
            println("Bob shared key : $it")
            println("Bob shared key: $bobSharedKey")
        }

        println("Equality : ${aliceSharedKey == bobSharedKey}")
        return aliceSharedKey == bobSharedKey
    }
}
