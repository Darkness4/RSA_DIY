# RSA DIY with Large Numbers

## Dépendances

- Java 11

## Lancer le main

```
./gradlew run --args='<c> <d> <n>'
```

Remplacez :

- \<c\> par le message chiffré en base 10
- \<d\> par le clé en base 10
- \<n\> par le modulo en base 10

## Lancer les tests unitaires

```
./gradlew test
```

## Structure du code source

- [`./src/main/kotlin/me.nguye.number`](./src/main/kotlin/me/nguye/number) contient les représentations grand nombre et points sur courbe elliptique.

- [`./src/main/kotlin/me.nguye.ecc`](./src/main/kotlin/me/nguye/ecc) contient les paramètres de courbes ECC.
- [`./src/main/kotlin/me.nguye.rsadiy`](./src/main/kotlin/me/nguye/rsadiy) contient les classes utilitaires de RSA et ECC. Ce sont ces classes que l'utilisateur utilise.
- [`./src/main/kotlin/me.nguye.utils`](./src/main/kotlin/me/nguye/utils) contiennent des classes utilitaires pour le développeur.

## Utilisation

### RSA

```kotlin
@ExperimentalUnsignedTypes
object Rsa {
    fun decrypt(c: BigUInt, d: BigUInt, n: BigUInt) = c.modPow(d, n)

    fun encrypt(m: BigUInt, e: BigUInt, n: BigUInt) = m.modPow(e, n)
}
```

Il faut connaitre les paramètres avant de l'exécuter. Exemple d'utilisation :

```kotlin
import me.nguye.number.BigUInt
import me.nguye.rsadiy.Rsa

fun main() {
     val c = BigUInt.valueOf("2967CB2D53ACF0D909D95BA2D4EA606C3BD8133706E74CE9EE70D8904B30D52ED481BD957F533A192DF2AFE1F72FBA4366A6D690C5E0C3D3721A3C68DB0E12494DE52B25F2487C5DE449C73E5142982877E02088274FE79AFD0C6FE037729B1266F2FA9CC577975611B34D92AE9AAC6839797F54EB2ABDBB36D1E1D5995A7C2E", radix=16)
                val d = BigUInt.valueOf("942E315D898EA7934F2B8C233E0529E7D4E32B206679EBBA31D18F803F077C3AC9599226A0279FACF10B9958507ACF7E2F43811E69E90A4D185E962D211240245FF4FB9873731D0655FE559ED2FF3C9412B1A64CB3AA510A4F5DAA9C01410AED01482F493545BDE0AE978F972B39DC7691B67C06D645A164511EDA0CAB6A68DD", radix=16)
                val n = BigUInt.valueOf("1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000DC00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002A7B", radix=16)

    // Usage
    val m = Rsa.decrypt(c, d, n)
    println("m=$m")
}
```

```sh
m=7b
```

Ou depuis base64 :

```kotlin
fun main() {
    val c = BigUInt.fromBase64String("KWfLLVOs8NkJ2Vui1OpgbDvYEzcG50zp7nDYkEsw1S7Ugb2Vf1M6GS3yr+H3L7pDZqbWkMXgw9NyGjxo2w4SSU3lKyXySHxd5EnHPlFCmCh34CCIJ0/nmv0Mb+A3cpsSZvL6nMV3l1YRs02SrpqsaDl5f1TrKr27NtHh1ZlafC4=")
    val d = BigUInt.fromBase64String("lC4xXYmOp5NPK4wjPgUp59TjKyBmeeu6MdGPgD8HfDrJWZImoCefrPELmVhQes9+L0OBHmnpCk0YXpYtIRJAJF/0+5hzcx0GVf5VntL/PJQSsaZMs6pRCk9dqpwBQQrtAUgvSTVFveCul4+XKzncdpG2fAbWRaFkUR7aDKtqaN0=")
    val n = BigUInt.fromBase64String("AQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACp7")

    // Usage
    val m = Rsa.decrypt(c, d, n)
    println("m=$m")
    println("m=${m.toBase64String()}")
}
```

```sh
m=7b
m=ew==
```

### ECC

```kotlin
@ExperimentalUnsignedTypes
class Edch(private val curve: Curve) {
    fun generatePrivateKey(): BigUInt = BigUInt.randomBelow(curve.n)

    fun generatePublicKey(privateKey: BigUInt): PointEcc = curve.generator * privateKey

    fun generateSharedKey(theirPublicKey: PointEcc, yourPrivateKey: BigUInt): PointEcc = theirPublicKey * yourPrivateKey

    @ExperimentalTime
    fun fakeKeyExchange(): Boolean {
        // ...
    }
}

```

La méthode `fakeKeyExchange` simule un échange de clé.

```kotlin
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
```

L'usage se résume donc à :

```kotlin
import me.nguye.ecc.curves.Secp192r1
import me.nguye.rsadiy.Edch

fun main() {
    val session = Edch(Secp192r1)
    val alicePrivateKey = session.generatePrivateKey()
    val alicePublicKey = session.generatePublicKey(alicePrivateKey)
    println("Alice private key: $alicePrivateKey")
    println("Alice public key: $alicePublicKey")

    val bobPrivateKey = session.generatePrivateKey()
    val bobPublicKey = session.generatePublicKey(bobPrivateKey)
    println("Bob private key: $bobPrivateKey")
    println("Bob public key: $bobPublicKey")

    println("Key Exchange !!!")

    val aliceSharedKey = session.generateSharedKey(bobPublicKey, alicePrivateKey)
    val bobSharedKey = session.generateSharedKey(alicePublicKey, bobPrivateKey)
    
    println("Equality : ${aliceSharedKey == bobSharedKey}")
}
```

```kotlin
Alice private key: 107c87c93c9c1e91f10b82ca242a39f2e7526c158c27a1a9202a504
Alice public key: PointEcc(x=5aaf57036fc016fd027ef426e2f93cce108a20871888226, y=aab1f72df6907769004d7572b28f7e28bafa43ec1c8ff4a9, curve=me.nguye.ecc.curves.Secp192r1@65397641)
Bob private key: 13b80e9f62f163a7fe21a899b4602b184310d9f2890f31e6d8a1e0b
Bob public key: PointEcc(x=fd101fe430b4c2ec857b0aa4e8478d848d6cdf3600cc0c70, y=120c135da7701f9e8e5bee0f94ff85a7db426963f4cc7ec6, curve=me.nguye.ecc.curves.Secp192r1@65397641)
Key Exchange !!!
Equality : true
```



