fun main() {
    val glossary = Glossary()
    glossary.readAllFiles()

    println(glossary.searchPhrase(listOf("drawing", "room", "was", "gradually")))

    println(glossary.searchPhraseWithDistance("gradually /3 drawing"))
}
