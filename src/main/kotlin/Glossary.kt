import Words.GlossaryWord
import Words.GlossaryWordDistance
import bTreeNodes.GlossaryNodeDistanceRoot
import bTreeNodes.GlossaryNodeKeyRoot
import bTreeNodes.GlossaryNodeRoot
import bTreeNodes.GlossaryNodeZoneDistanceRoot
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
    val fileReader = FileReaderCustom(charset)


    private val mapOf3GramWord = HashMap<String, ArrayList<GlossaryWord>>()
    private val mapOf2GramWord = HashMap<String, ArrayList<GlossaryWord>>()
    private val rootGlossaryNodePhrase = GlossaryNodeRoot()
    private val rootGlossaryNodeSwap = GlossaryNodeKeyRoot()
    private val rootGlossaryNodeDistance = GlossaryNodeDistanceRoot()
    private val rootGlossaryNodeZoneDistance = GlossaryNodeZoneDistanceRoot()


    private val mapFileIndex = HashMap<Int, String>()
    private var countOfFiles = 0


    fun readAllFiles(folderPath: String = "src/main/resources/texts/books/") {
        logger?.info("Start reading from directory (path: $folderPath)")
        File(folderPath).walk().forEach { file ->
            if (file.isDirectory) {
                logger?.debug("File with path ${file.name} is directory")
            } else {
                mapFileIndex[countOfFiles++] = file.name
                val fileExtension = FileExtension.valueOf(file.name.substringAfterLast("."))
                when (fileExtension) {
                    FileExtension.txt -> {
                    }
                    FileExtension.fb2 -> readAllZoneDistWordFB2(file)
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
            res = findIntersectionWithWordDistance(
                res,
                listOfGlossaryWord[wordIndex],
                listOfGlossaryWord[wordIndex + 1],
                splitList[distance].replace("/", "").toInt()
            )
        }
        return res
    }

    private fun findIntersectionWithWordDistance(
        enterList: List<String>,
        word1: GlossaryWordDistance,
        word2: GlossaryWordDistance,
        dis: Int
    ): List<String> {
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

    private fun readAllWordWithGramFromTxtFile(
        file: File,
        gram: Int = 3,
        map: HashMap<String, ArrayList<GlossaryWord>>
    ) {
        val newWords = fileReader.readTxtFileWord(file)
        logger?.info("File (${file.name}) contains ${newWords.size} unique words.")

        newWords.keys.forEach { word ->
            newWords[word]?.let { insertWithGramWord(word, it, file.name.substringAfterLast("/"), gram, map) }
        }
    }

    private fun insertWithGramWord(
        word: String,
        wordRepeat: Int,
        fileName: String,
        gram: Int,
        map: HashMap<String, ArrayList<GlossaryWord>>
    ) {
        val wordKey = "$$word$"
        for (i in 0..wordKey.length - gram) {
            val mapKey = wordKey.substring(i, i + gram)
            val arrayOfWord = map.getOrPut(mapKey) { arrayListOf() }
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
        rootGlossaryNodePhrase.insert(word, fileIndex = fileName.toLong(), fileRepeat = fileRepeat)
    }

    fun searchPhrase(phrase: String): List<Long> {
        return searchPhrase(phrase.split(" "))
    }

    fun searchPhrase(wordList: List<String>): List<Long> {
        if (wordList.size == 2)
            return rootGlossaryNodePhrase.get("${wordList[0]} ${wordList[1]}")?.mapFileCount?.keys?.let { it.toList() }
                ?: emptyList()

        val listOfFiles = LinkedList<List<Long>>()
        for (i in 0 until wordList.size - 1) {
            rootGlossaryNodePhrase.get("${wordList[i]} ${wordList[i + 1]}")?.let {
                listOfFiles.add(it.mapFileCount.keys.toList())
            } ?: listOfFiles.add(emptyList())
        }

        /*return mapFileIndex.values.toList()

        listOfFiles.forEach { list ->
            res = findIntersection(res, list)
        }*/
        return emptyList()
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

    //region two Joker
    public fun findWordWithJokers(wordWithJoker: String): ArrayList<GlossaryWord> {
        val listOfLists = ArrayList<ArrayList<GlossaryWord>>()
        var tempWord = wordWithJoker
        if (!wordWithJoker.startsWith("*")) tempWord = "$$tempWord"
        if (!wordWithJoker.endsWith("*")) tempWord = "$tempWord$"
        val partsOfWord = tempWord.split("*")

        partsOfWord.forEach { partOfWord ->
            when (partOfWord.length) {
                1 -> return ArrayList<GlossaryWord>()
                2 -> mapOf2GramWord[partOfWord]?.let { listOfLists.add(it) }
                else -> {
                    for (j in 0..partOfWord.length - 3) {
                        val mapKey = partOfWord.substring(j, j + 3)
                        mapOf3GramWord[mapKey]?.let { listOfLists.add(it) }
                    }
                }
            }
        }

        var res = listOfLists[0]

        for (i in 1 until listOfLists.size) {
            res = findIntersectionGlossary(res, listOfLists[i])
        }

        return res
    }
    //endregion

    //region one Joker

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

    //endregion

    fun readAllZoneDistWordFB2(file: File) {
        val newWords = fileReader.readFB2File(file)
        logger?.info("File (${file.name}) contains ${newWords.size} unique words.")
        newWords.keys.forEach { word ->
            newWords[word]?.let { insertWordWithZoneDistance(word, it, file.name.substringAfterLast("/")) }
        }
    }

    private fun insertWordWithZoneDistance(
        word: String,
        distanceList: LinkedList<Pair<Int, ArrayList<Int>>>,
        fileName: String
    ) {
        rootGlossaryNodeZoneDistance.insert(word, fileName = fileName, fileListDistance = distanceList)
    }

    private fun findIntersection(mainList: List<String>, additionList: List<String>): List<String> {
        val resList = LinkedList<String>()

        mainList.forEach {
            if (additionList.contains(it))
                resList.add(it)
        }

        return resList
    }

    private fun findIntersectionGlossary(
        mainList: List<GlossaryWord>,
        additionList: List<GlossaryWord>
    ): ArrayList<GlossaryWord> {
        val resList = ArrayList<GlossaryWord>()

        mainList.forEach {
            if (additionList.contains(it))
                resList.add(it)
        }

        return resList
    }

    fun searchFilesZones(author: Array<String>, titles: Array<String>, body: Array<String>): List<Pair<String, Double>> {
        val authorWordList = HashMap<String, ArrayList<String>>()
        author.forEach { s ->
            rootGlossaryNodeZoneDistance.get(s)?.let {
                it.mapFileCount.forEach lit@{ entry ->
                    entry.value.forEach { pair ->
                        if (pair.second.contains(0)) {
                            authorWordList.getOrPut(s) { ArrayList() }.add(entry.key)
                            return@lit
                        }
                    }
                }
            }
        }
        val titlesWordList = HashMap<String, ArrayList<String>>()
        titles.forEach { s ->
            rootGlossaryNodeZoneDistance.get(s)?.let {
                it.mapFileCount.forEach lit@{ entry ->
                    entry.value.forEach { pair ->
                        if (pair.second.contains(1)) {
                            titlesWordList.getOrPut(s) { ArrayList() }.add(entry.key)
                            return@lit
                        }
                    }
                }
            }
        }
        val bodyWordList = HashMap<String, ArrayList<String>>()
        body.forEach { s ->
            rootGlossaryNodeZoneDistance.get(s)?.let {
                it.mapFileCount.forEach lit@{ entry ->
                    entry.value.forEach { pair ->
                        if (pair.second.contains(2)) {
                            bodyWordList.getOrPut(s) { ArrayList() }.add(entry.key)
                            return@lit
                        }
                    }
                }
            }
        }

        val res = HashMap<String, DoubleArray>()
        mapFileIndex.map { res.put(it.value, doubleArrayOf(0.0, 0.0, 0.0)) }

        res.keys.forEach { fileName ->
            res[fileName]!![0] =
                cosineSimilarity(
                    DoubleArray(author.size) { i ->
                        if (authorWordList[author[i]]?.contains(fileName) ?: false) 1.0 else 0.0
                    }, DoubleArray(author.size) {
                        1.0
                    }
                )
            res[fileName]!![1] =
                cosineSimilarity(
                    DoubleArray(titles.size) { i ->
                        if (titlesWordList[titles[i]]?.contains(fileName) ?: false) 1.0 else 0.0
                    }, DoubleArray(titles.size) {
                        1.0
                    }
                )
            res[fileName]!![2] =
                cosineSimilarity(
                    DoubleArray(body.size) { i ->
                        if (bodyWordList[body[i]]?.contains(fileName) ?: false) 1.0 else 0.0
                    }, DoubleArray(body.size) {
                        1.0
                    }
                )
        }

        return res.map { entry -> Pair(entry.key, entry.value[0] * a + entry.value[1] * t + entry.value[2] * b) }
            .sortedBy { pair -> pair.second }.reversed()

    }

    val a = 0.4
    val t = 0.4
    val b = 0.2

    fun cosineSimilarity(vectorA: DoubleArray, vectorB: DoubleArray): Double {
        var dotProduct = 0.0
        var normA = 0.0
        var normB = 0.0
        for (i in vectorA.indices) {
            dotProduct += vectorA[i] * vectorB[i]
            normA += Math.pow(vectorA[i], 2.0)
            normB += Math.pow(vectorB[i], 2.0)
        }
        if (normA == 0.0 || normB == 0.0) return 0.0
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))
    }
}
