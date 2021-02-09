import BTreeNodes.GlossaryNodeDistanceRoot
import BTreeNodes.GlossaryNodeKeyRoot
import BTreeNodes.GlossaryNodePhraseRoot
import org.slf4j.Logger
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

class Glossary(
    private val charset: Charset = Charset.defaultCharset(),
    private val logger: Logger? = null
) {
    val fileNameGlossary: String = "Glossary.txt"
    val fileReader = FileReader(charset)


    private val mapOf3GramWord = HashMap<String, ArrayList<GlossaryWord>>()
    private val rootGlossaryNodePhrase = GlossaryNodePhraseRoot()
    private val rootGlossaryNodeSwap = GlossaryNodeKeyRoot()
    private val rootGlossaryNodeDistance = GlossaryNodeDistanceRoot()

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
                    FileExtension.txt -> {
                        readAllWordFromTxtFile(file)
                    }
                    //FileExtension.fb2 -> readAllWordsFromFB2File(file)
                    else -> logger?.warn("File with extension $this not supported")
                }
            }
        }
    }

    //region Word with distance

    fun readAllWordsWithDistanceFromTxtFile(file: File) {
        val newWords = fileReader.readTxtFileWithDistance(file)
        logger?.info("File (${file.name}) contains ${newWords.size} unique words.")
        newWords.keys.forEach { word ->
            newWords[word]?.sortBy { it }
            newWords[word]?.let { insertWordWithDistance(word, it, file.name.substringAfterLast("/")) }
        }
    }

    private fun insertWordWithDistance(word: String, distanceList: LinkedList<Int>, fileName: String) {
        rootGlossaryNodeDistance.insert(word, fileName = fileName, fileListDistance = distanceList)
    }

    fun searchPhraseWithDistance(phrase: String): List<String> {
        val splitList = phrase.split(" ")
        val listOfGlossaryWord = LinkedList<GlossaryWordDistance>()
        splitList.forEach { word ->
            if (!word.startsWith("/"))
                rootGlossaryNodeDistance.get(word)?.let { listOfGlossaryWord.add(it) }
                    ?: return emptyList()
        }

        var res = mapFileIndex.values.toList()
        listOfGlossaryWord.forEach { glossaryWordDistance ->
            res = findIntersection(res, glossaryWordDistance.mapFileCount.keys.toList())
        }
        if (res.isEmpty())
            return res

        for ((wordIndex, distance) in (0 until listOfGlossaryWord.size - 1).zip(1..splitList.size step 2)) {
            res = findIntersectionWithWordDistance(res, listOfGlossaryWord[wordIndex], listOfGlossaryWord[wordIndex + 1], splitList[distance].replace("/", "").toInt())
        }
        return res
    }

    private fun findIntersectionWithWordDistance(enterList: List<String>, word1: GlossaryWordDistance, word2: GlossaryWordDistance, dis: Int): List<String> {
        val res = LinkedList<String>()
        enterList.forEach { fileName ->
            val iteratorList1 = word1.mapFileCount[fileName]!!.iterator()
            val iteratorList2 = word2.mapFileCount[fileName]!!.iterator()

            var next1 = iteratorList1.next()
            var next2 = iteratorList2.next()
            do {
                if (abs(next1 - next2) <= dis) {
                    res.add(fileName)
                    break
                }
                if (next1 < next2 && iteratorList1.hasNext())
                    next1 = iteratorList1.next()
                else if (next1 > next2 && iteratorList2.hasNext())
                    next2 = iteratorList2.next()
                else
                    break
            } while ((iteratorList1.hasNext() || iteratorList2.hasNext()) && !res.contains(fileName))
        }
        return res
    }

    public fun saveGlossaryWithDistance(fileDirectory: String = "src/main/resources/GlossaryFolder/") {
        val file = File(fileDirectory + "GlossaryDis.txt")
        file.createNewFile()
        val stringBuilder = StringBuilder()
        val list = rootGlossaryNodeDistance.getAll()
        list.forEach { glossaryWordDistance ->
            stringBuilder.append(glossaryWordDistance.toSaveFormat()).append("\n")
        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)
        file.writeText(stringBuilder.toString(), charset)
    }

    //endregion

    //region Word with 3 gram

    private fun readAllWord3GramFromTxtFile(file: File) {
        val newWords = fileReader.readTxtFileWord(file)
        logger?.info("File (${file.name}) contains ${newWords.size} unique words.")

        newWords.keys.forEach { word ->
            newWords[word]?.let { insert3GramWord(word, it, file.name.substringAfterLast("/")) }
        }
    }

    private fun insert3GramWord(word: String, wordRepeat: Int, fileName: String) {
        val wordKey = "$$word$"
        for (i in 0..wordKey.length - 3) {
            val mapKey = wordKey.substring(i, i + 3)
            val arrayOfWord = mapOf3GramWord.getOrPut(mapKey) { arrayListOf() }
            val wordIndex = arrayOfWord.binarySearch { glossaryWord ->
                glossaryWord.value.compareTo(word)
            }
            if (wordIndex < 0 || arrayOfWord[wordIndex].value != word) {
                val newWord = GlossaryWord(word)
                newWord.processNewFile(fileName, wordRepeat)
                arrayOfWord.add(newWord)
            } else
                arrayOfWord[wordIndex].processNewFile(fileName, wordRepeat)
            arrayOfWord.sortBy { glossaryWord -> glossaryWord.value }
        }
    }

    private fun search3Gram(searchWord: String): GlossaryWord? {
        mapOf3GramWord.get(searchWord.substring(3))?.let { arrayList ->
            val wordIndex = arrayList.binarySearch { glossaryWord ->
                glossaryWord.value.compareTo(searchWord)
            }
            if (arrayList[wordIndex].value == searchWord)
                return arrayList[wordIndex]
        }
        return null
    }

    //endregion

    //region Word with swapping

    private fun readAllWordFromTxtFile(file: File) {
        val newWords = fileReader.readTxtFileWord(file)

        newWords.keys.forEach { word ->
            newWords[word]?.let { insertWordSwap(word, it, file.name.substringAfterLast("/")) }
        }
    }

    private fun insertWordSwap(word: String, fileRepeat: Int, fileName: String) {
        var tempWord = "$word$"
        repeat(tempWord.length) {
            rootGlossaryNodeSwap.insert(tempWord, word, fileName = fileName, fileRepeat = fileRepeat)
            tempWord = tempWord.substring(1) + tempWord[0]
        }
    }

    public fun searchWordSwap(word: String): GlossaryWord? {
        return rootGlossaryNodeSwap.get("$word$")
    }

    //endregion

    //region Word in phrase

    private fun readAllPhraseFromTxtFile(file: File) {
        val newWords = fileReader.readTxtFilePhrase(file)
        logger?.info("File (${file.name}) contains ${newWords.size} unique words.")

        newWords.keys.forEach { word ->
            newWords[word]?.let { insertPhrase(word, it, file.name.substringAfterLast("/")) }
        }
    }

    private fun insertPhrase(word: String, fileRepeat: Int, fileName: String) {
        rootGlossaryNodePhrase.insert(word, fileName = fileName, fileRepeat = fileRepeat)
    }

    fun searchPhrase(phrase: String): List<String> {
        return searchPhrase(phrase.split(" "))
    }

    fun searchPhrase(wordList: List<String>): List<String> {
        if (wordList.size == 2)
            return rootGlossaryNodePhrase.get("${wordList[0]} ${wordList[1]}")?.mapFileCount?.keys?.let { it.toList() } ?: emptyList()

        val listOfFiles = LinkedList<List<String>>()
        for (i in 0 until wordList.size - 1) {
            rootGlossaryNodePhrase.get("${wordList[i]} ${wordList[i + 1]}")?.let {
                listOfFiles.add(it.mapFileCount.keys.toList())
            } ?: listOfFiles.add(emptyList())
        }

        var res = mapFileIndex.values.toList()

        listOfFiles.forEach { list ->
            res = findIntersection(res, list)
        }
        return res
    }

    public fun saveGlossaryPhrase(fileDirectory: String = "src/main/resources/GlossaryFolder/") {
        val file = File(fileDirectory + "GlossaryPhrase.txt")
        file.createNewFile()
        val stringBuilder = StringBuilder()
        val list = rootGlossaryNodePhrase.getAll()
        list.forEach { glossaryWordDistance ->
            stringBuilder.append(glossaryWordDistance.toSaveFormat()).append("\n")
        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)
        file.writeText(stringBuilder.toString(), charset)
    }

    //endregion

    //region search with Joker

    public fun findWordWithJoker(wordWithJoker: String): ArrayList<GlossaryWord> {
        if (!wordWithJoker.contains("*")) return ArrayList()

        return when (true) {
            wordWithJoker.startsWith("*") -> findWordWithStartJoker(wordWithJoker)
            wordWithJoker.endsWith("*") -> findWordWithEndJoker(wordWithJoker)
            else -> findWordWithMidJoker(wordWithJoker)
        }
    }

    private fun findWordWithStartJoker(wordWithJoker: String): ArrayList<GlossaryWord> {
        val wordWithoutWord = wordWithJoker.replace("*", "")
        return rootGlossaryNodeSwap.getAllAfter("$wordWithoutWord$")
    }

    private fun findWordWithEndJoker(wordWithJoker: String): ArrayList<GlossaryWord> {
        val wordWithoutWord = wordWithJoker.replace("*", "")
        return rootGlossaryNodeSwap.getAllAfter("$$wordWithoutWord")
    }

    private fun findWordWithMidJoker(wordWithJoker: String): ArrayList<GlossaryWord> {
        val startPhrase = wordWithJoker.split("*")[0]
        val endPhrase = wordWithJoker.split("*")[1]
        return rootGlossaryNodeSwap.getAllAfter("$endPhrase$$startPhrase")
    }

    //endregion

    private fun findIntersection(mainList: List<String>, additionList: List<String>): List<String> {
        val resList = LinkedList<String>()

        mainList.forEach {
            if (additionList.contains(it))
                resList.add(it)
        }

        return resList
    }

    private fun findIntersectionGlossary(mainList: List<GlossaryWord>, additionList: List<GlossaryWord>): ArrayList<GlossaryWord> {
        val resList = ArrayList<GlossaryWord>()

        mainList.forEach {
            if (additionList.contains(it))
                resList.add(it)
        }

        return resList
    }

}
