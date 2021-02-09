fun main() {
    val glossary = Glossary()
    glossary.readAllFiles()
    glossary.findWordWithJoker("s*s").forEach {
        println(it.value)
    }

}
