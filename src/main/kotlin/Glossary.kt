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


    private val mapOfWord = HashMap<String, ArrayList<GlossaryWord>>()
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
                    //FileExtension.fb2 -> readAllWordsFromFB2File(file)
                    else -> logger?.warn("File with extension $this not supported")
                }
            }
        }
    }

    fun readAllWordsFromTxtFile(file: File) {
        val newWords = fileReader.readTxtFile(file)
        logger?.info("File (${file.name}) contains ${newWords.size} unique words.")
        newWords.keys.forEach { word ->
            newWords[word]?.sortBy { it }
            newWords[word]?.let { insertWord(word, it, file.name.substringAfterLast("/")) }
        }
    }

    private fun insertWord(word: String, listPosition: LinkedList<Int>, fileName: String) {
        val wordKey = "$$word$"
        for (i in 0 .. wordKey.length - 2) {
            val mapKey = wordKey.substring(i, i + 2)
            val arrayOfWord = mapOfWord.getOrPut(mapKey) { arrayListOf() }
            val wordIndex = arrayOfWord.binarySearch { glossaryWord ->
                glossaryWord.value.compareTo(word)
            }
            if (wordIndex < 0 || arrayOfWord[wordIndex].value != word) {
                val newWord = GlossaryWord(word)
                newWord.processNewFile(fileName, listPosition)
                arrayOfWord.add(newWord)
            } else
                arrayOfWord[wordIndex].processNewFile(fileName, listPosition)
        }
    }

    /*fun readAllWordsFromFB2File(file: File) {
        val newWords = fileReader.readFB2File(file)
        logger?.info("File (${file.name}) contains ${newWords.size} unique words.")
        newWords.keys.forEach { word ->
            insertWord(word, newWords[word]!!, file.name.substringAfterLast("/"))
        }
    }*/

    /*fun writeToDisk(fileDirectory: String = "src/main/resources/") {
        val file = File(fileDirectory + fileNameGlossary)
        file.createNewFile()
        val stringBuilder = StringBuilder()
        for (i in 0 until size) {
            stringBuilder.append(wordsArray[i]?.toSaveFormat()).append("\n")
        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)
        file.writeText(stringBuilder.toString(), charset)
    }*/

    fun getSize(): Int {
        return mapOfWord.size
    }

    fun getCountOfAllWords(): Long {
        /*var result = 0L
        wordsArray.forEach { word ->
            word?.let { result += it.allWordCount() }
        }
        return result*/
        return 0
    }

    fun getGlossaryFileSize(fileDirectory: String = "src/main/resources/"): Long {
        val file = File(fileDirectory + fileNameGlossary)
        return file.length()
    }

    fun getTextDirectorySize(fileDirectory: String = "src/main/resources/texts/"): Long {
        val file = File(fileDirectory)
        return file.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }

    enum class SearchPredicate {
        AND,
        OR
    }

    private fun search(searchWord: String): GlossaryWord? {
        mapOfWord.get(searchWord.substring(2))?.let { arrayList ->
            val wordIndex = arrayList.binarySearch { glossaryWord ->
                glossaryWord.value.compareTo(searchWord)
            }
            if (arrayList[wordIndex].value == searchWord)
                return arrayList[wordIndex]
        }
        return null
    }

    //    fun search(words: List<String>, searchPredicate: SearchPredicate = SearchPredicate.AND): List<String> {
    //        val listMultipleFiles: LinkedList<List<String>> = LinkedList()
    //        words.forEach {
    //            search(it)?.let {
    //                listMultipleFiles.add(it)
    //            } ?: listMultipleFiles.add(emptyList())
    //        }
    //
    //        if (searchPredicate == SearchPredicate.AND) {
    //            listMultipleFiles.sortBy { listMultipleFiles.size } //Сортуємо множини за величиною для того щоб
    //            var resList = mapFileIndex.values.toList()
    //            listMultipleFiles.forEach {
    //                resList = findIntersection(resList, it)
    //            }
    //            return resList
    //        }
    //        val resultList: LinkedList<String> = LinkedList<String>()
    //        listMultipleFiles.forEach { list ->
    //            list.forEach { fileName ->
    //                if (!resultList.contains(fileName))
    //                    resultList.add(fileName)
    //            }
    //        }
    //        return resultList
    //    }

    private fun findIntersection(mainList: List<String>, additionList: List<String>): List<String> {
        val resList = LinkedList<String>()

        mainList.forEach {
            if (additionList.contains(it))
                resList.add(it)
        }

        return resList
    }
}