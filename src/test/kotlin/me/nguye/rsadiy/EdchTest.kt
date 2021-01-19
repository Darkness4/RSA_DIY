package me.nguye.rsadiy

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.booleans.shouldBeTrue
import me.nguye.ecc.curves.Secp192r1
import me.nguye.ecc.curves.Secp521r1

@ExperimentalUnsignedTypes
class EdchTest : WordSpec({

    "fakeKeyExchange" should {
        "work on Secp192r1" {
            val session = Edch(Secp192r1)

            session.fakeKeyExchange().shouldBeTrue()
        }

        "work on Secp521r1" {
            val session = Edch(Secp521r1)

            session.fakeKeyExchange().shouldBeTrue()
        }
    }
})
