fun main() {
    val glossary = Glossary()
    glossary.readAllFiles()
    glossary.searchFilesZones(
        author = arrayOf("a"),
        titles = arrayOf("order"),
        body = arrayOf("gfrgthhyjukj")
    ).forEach (::println)
}
