import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import me.nguye.number.BigUInt
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Run : CliktCommand() {
    private val c: String by argument(help = "Message")
    private val d: String by argument(help = "Rsa Key")
    private val n: String by argument(help = "Modulo")

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    override fun run() {
        var result: BigUInt
        measureTime {
            val d = BigUInt.valueOf(d)
            val c = BigUInt.valueOf(c)
            val n = BigUInt.valueOf(n)
            result = c.modPow(d, n)
        }.also {
            println("m = $result, Time Elapsed : $it")
            File("FirstCallRsa.txt").appendText("${it.toLongNanoseconds()}\n")
        }

        measureTime {
            val d = BigUInt.valueOf(d)
            val c = BigUInt.valueOf(c)
            val n = BigUInt.valueOf(n)
            result = c.modPow(d, n)
        }.also {
            println("m = $result, Time Elapsed : $it")
            File("SecondCallRsa.txt").appendText("${it.toLongNanoseconds()}\n")
        }
    }
}

fun main(args: Array<String>) = Run().main(args)
