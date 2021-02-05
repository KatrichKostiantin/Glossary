fun main() {
    val glossary = Glossary()
    glossary.readAllFiles()

    println(glossary.searchPhrase(listOf("drawing", "room")))

    println(glossary.searchPhraseWithDistance("drawing /1 room /3 drawing"))
    glossary.saveGlossaryPhrase()
}
