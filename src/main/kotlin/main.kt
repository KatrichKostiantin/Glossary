fun main() {
    val glossary = Glossary()
    glossary.readAllFiles()
    glossary.searchFilesZones(
        author = arrayOf("a"),
        titles = arrayOf("and", "order"),
        body = arrayOf("heyheyheyhey", "the")
    ).forEach (::println)
}
