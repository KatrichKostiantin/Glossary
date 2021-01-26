import org.slf4j.Logger
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap

class Glossary(
    private val charset: Charset = Charset.defaultCharset(),
    private val logger: Logger? = null
) {
    val fileNameGlossary: String = "Glossary.txt"
    val fileReader = FileReader(charset)

    var wordsArray = arrayOfNulls<GlossaryWord>(10)
    private var size = 0
    private var capacity = 10
    private val mapOfWordIndex = HashMap<String, Int>()
    private val mapFileIndex = HashMap<Int, String>()
    private var countOfFiles = 0

    private enum class FileExtension {
        txt,
        fb2,
    }

    fun readAllFiles(folderPath: String = "src/main/resources/texts/") {
        logger?.info("Start reading from directory (path: $folderPath)")
        File(folderPath).walk().forEach { file ->
            if (file.isDirectory) {
                logger?.debug("File with path ${file.name} is directory")
            } else {
                mapFileIndex[countOfFiles++] = file.name
                val fileExtension = FileExtension.valueOf(file.name.substringAfterLast("."))
                when (fileExtension) {
                    FileExtension.txt -> readAllWordsFromTxtFile(file)
                    FileExtension.fb2 -> readAllWordsFromFB2File(file)
                    else -> logger?.warn("File with extension $this not supported")
                }
            }
        }
    }

    fun readAllWordsFromTxtFile(file: File) {
        val newWords = fileReader.readTxtFile(file)
        logger?.info("File (${file.name}) contains ${newWords.size} unique words.")
        newWords.keys.forEach { word ->
            insertWord(word, newWords[word]!!, file.name.substringAfterLast("/"))
        }
    }

    fun readAllWordsFromFB2File(file: File) {
        val newWords = fileReader.readFB2File(file)
        logger?.info("File (${file.name}) contains ${newWords.size} unique words.")
        newWords.keys.forEach { word ->
            insertWord(word, newWords[word]!!, file.name.substringAfterLast("/"))
        }
    }

    private fun insertWord(word: String, count: Int, fileName: String) {
        mapOfWordIndex[word]?.let {
            wordsArray[it]?.processNewFile(fileName, count)
        } ?: run {
            if (size >= capacity)
                increaseArray(capacity * 2)
            wordsArray[size] = GlossaryWord(word).apply {
                this.processNewFile(fileName, count)
            }
            mapOfWordIndex[word] = size
            size++
        }
    }

    private fun increaseArray(newSize: Int) {
        val newArray = arrayOfNulls<GlossaryWord>(newSize)
        wordsArray.copyInto(newArray)
        wordsArray = newArray
        capacity = newSize
    }

    fun writeToDisk(fileDirectory: String = "src/main/resources/") {
        val file = File(fileDirectory + fileNameGlossary)
        file.createNewFile()
        val stringBuilder = StringBuilder()
        for (i in 0 until size) {
            stringBuilder.append(wordsArray[i]?.toSaveFormat()).append("\n")
        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)
        file.writeText(stringBuilder.toString(), charset)
    }

    fun getSize(): Int {
        return size
    }

    fun getCountOfAllWords(): Long {
        var result = 0L
        wordsArray.forEach { word ->
            word?.let { result += it.allWordCount() }
        }
        return result
    }

    fun getGlossaryFileSize(fileDirectory: String = "src/main/resources/"): Long {
        val file = File(fileDirectory + fileNameGlossary)
        return file.length()
    }

    fun getTextDirectorySize(fileDirectory: String = "src/main/resources/texts/"): Long {
        val file = File(fileDirectory)
        return file.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }

    fun getIdentityMatrix(): Array<Array<Byte?>> {
        return Array(size) { wordIndex ->
            Array(countOfFiles) { fileIndex ->
                if (wordsArray[wordIndex]?.mapFileCount?.contains(mapFileIndex[fileIndex]) == true)
                    1
                else
                    0
            }
        }
    }

    enum class SearchPredicate {
        AND,
        OR
    }

    fun search(words: List<String>, searchPredicate: SearchPredicate = SearchPredicate.AND): MutableCollection<String> {
        val glossaryWordList: LinkedList<GlossaryWord> = LinkedList()
        words.forEach {
            mapOfWordIndex.get(it)?.let { wordIndex ->
                glossaryWordList.add(wordsArray.get(wordIndex)!!)
            }
        }

        if (searchPredicate == SearchPredicate.AND) {
            glossaryWordList.sortBy { it.mapFileCount.size } //Сортуємо множини за величиною для того щоб
            val resultList = mapFileIndex.values
            glossaryWordList.forEach { glossaryWord ->
                resultList.retainAll(glossaryWord.mapFileCount.keys)
            }
            return resultList
        }
        val resultList: MutableCollection<String> = mutableListOf()
        glossaryWordList.forEach { glossaryWord ->
            glossaryWord.mapFileCount.keys.forEach { fileName ->
                if (!resultList.contains(fileName))
                    resultList.add(fileName)
            }
        }
        return resultList
    }
}