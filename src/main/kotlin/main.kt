fun main() {
    val glossary = Glossary()
    glossary.readAllFiles()
    println("Glossary unique word: ${glossary.getSize()}")
    println("Glossary all word: ${glossary.getCountOfAllWords()}")
    println("Glossary size in byte: ${glossary.getGlossaryFileSize()}, when texts directory size: ${glossary.getTextDirectorySize()}")

}
