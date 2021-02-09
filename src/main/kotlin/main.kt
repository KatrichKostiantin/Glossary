fun main() {
    val glossary = Glossary()
    glossary.readAllFiles()
    glossary.findWordWithJokers("s*ss").forEach {
        println(it.value)
    }

}
