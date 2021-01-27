fun main() {
    val glossary = Glossary()
    glossary.readAllFiles()
    glossary.writeToDisk()
    println("Glossary unique word: ${glossary.getSize()}")
    println("Glossary all word: ${glossary.getCountOfAllWords()}")
    println("Glossary size in byte: ${glossary.getGlossaryFileSize()}, when texts directory size: ${glossary.getTextDirectorySize()}")

    val map = glossary.getIdentityMatrix()

    println(glossary.search(listOf("oleg", "fffffffffffffffffffffffffffffff"), Glossary.SearchPredicate.AND))
    println(glossary.search(listOf("oleg", "1"), Glossary.SearchPredicate.AND))
    println(glossary.search(listOf("oleg", "dwdefrgeda"), Glossary.SearchPredicate.AND))
}
