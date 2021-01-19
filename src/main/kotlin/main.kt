import org.slf4j.LoggerFactory

fun main() {
    val glossary = Glossary()
    glossary.readAllFiles()
    glossary.writeToDisk()
}