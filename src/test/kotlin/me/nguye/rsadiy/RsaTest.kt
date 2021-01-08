package me.nguye.rsadiy

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import me.nguye.number.BigUInt
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
@ExperimentalUnsignedTypes
class RsaTest : WordSpec({
    "decrypt(Big, Big, Big)" should {
        "returns a good result fast from base 10 to base 2^31" {
            val expected = BigUInt.valueOf("123", 10)
            var result: BigUInt
            measureTime {
                val c = BigUInt.valueOf("29075891562236853554062599128328159590183028980552101085544262704780053549534418578507236855720567419975163282590047789761592080601496152946952125090156744601158717295382511431523328696904736152776043566580142204885912443831792077784183631398221826664611547239837936198939692178263167338882034607609865731118")
                val d = BigUInt.valueOf("104055844167107781248589752608920442121435852413004719607463950823987312821132759166809988685882849660028713452809154247360580185712259277529373755633843749387184470871012615224812078370633557809434904918225321388120741945280206181683834799019246740113882929623211747709365319857181807977455643688718851270877")
                val n = BigUInt.valueOf("179769313486231590772930519078902473361797697894230657273430081157732675805500963132708477322407536021120113879871393357658789768814416622492847430639477074095512480796227391561801824887394139579933613278628104952355769470429079061808809522886423955917442317693387325171135071792698344550223571732405562649211")
                result = Rsa.decrypt(c, d, n)
            }.also {
                println("m = $result, Time elapsed: $it")
                println("We are aiming at: 16ms from BigInteger")
            }

            result shouldBe expected
        }
    }
})
