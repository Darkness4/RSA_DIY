
import me.nguye.number.BigInt
import me.nguye.rsadiy.Rsa
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
@ExperimentalUnsignedTypes
fun main() {
    val c = BigInt.valueOf("101011100110", 2).toBase2PowK(17)
    val d = BigInt.valueOf("110011101", 2).toBase2PowK(17)
    val n = BigInt.valueOf("110010100001", 2).toBase2PowK(17)

    println("decrypting")
    measureTime {
        Rsa.decrypt(c, d, n).also { println(it) }
    }.also { println("Time Elapsed : $it") }
}
