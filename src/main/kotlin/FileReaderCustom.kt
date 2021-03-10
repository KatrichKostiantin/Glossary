import com.kursx.parser.fb2.FictionBook
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FileReaderCustom(
    private val charset: Charset = Charset.defaultCharset()
) {
    public fun readTxtFileWithDistance(file: File): Map<String, LinkedList<Int>> {
        val splitWordArray = readTxtAllWord(file)
        return getWordDistance(splitWordArray)
    }

    fun readFB2File(file: File): Map<String, LinkedList<Pair<Int, ArrayList<Int>>>> {
        val fictionFile = FictionBook(file)

        val body = StringBuilder()
        fictionFile.body.sections.forEach { section ->
            body.append(section.titles)
            section.elements.forEach { element ->
                body.append(element.text)
            }
        }

        val titleMap = getWordDistance(splitAndCleanWord(fictionFile.title.toString()))
        val authorMap = getWordDistance(splitAndCleanWord(fictionFile.description.titleInfo.authors.toString()))
        val bodyMap = getWordDistance(splitAndCleanWord(body.toString()))
        val res = mutableMapOf<String, LinkedList<Pair<Int, ArrayList<Int>>>>()
        bodyMap.keys.forEach { s ->
            val list = LinkedList<Pair<Int, ArrayList<Int>>>()
            authorMap[s]?.map { i -> list.add(Pair(i, arrayListOf(0))) }
            titleMap[s]?.map { i -> list.add(Pair(i, arrayListOf(1))) }
            bodyMap[s]?.map { i -> list.add(Pair(i, arrayListOf(2))) }
            list.sortBy { pair -> pair.first }

            var i = 0
            while (true) {
                if (i == list.size - 1) break
                if (list[i].first == list[i + 1].first) {
                    list[i].second.addAll(list[i + 1].second)
                    list.removeAt(i + 1)
                }
                i++
            }

            res.put(s, list)
        }

        return res
    }

    private fun getWordDistance(arr: ArrayList<String>): Map<String, LinkedList<Int>> {
        val listOfWords = mutableMapOf<String, LinkedList<Int>>()
        for (i in arr.indices) {
            val word = cleanWord(arr[i])
            if (word.isNotBlank())
                listOfWords.getOrPut(word) { LinkedList<Int>() }.add(i)
        }

        return listOfWords
    }

    public fun readTxtFilePhrase(file: File): Map<String, Int> {
        val listOfWords = mutableMapOf<String, Int>()
        val splitWordArray = readTxtAllWord(file)
        for (i in 0 until splitWordArray.size - 1) {
            val word = "${splitWordArray[i]} ${splitWordArray[i + 1]}"
            if (word.isNotBlank())
                listOfWords[word] = listOfWords.getOrPut(word) { 0 } + 1
        }

        return listOfWords
    }

    public fun readTxtFileWord(file: File): Map<String, Int> {
        val listOfWords = mutableMapOf<String, Int>()
        val splitWordArray = readTxtAllWord(file)
        for (i in splitWordArray.indices) {
            val word = cleanWord(splitWordArray[i])
            if (word.isNotBlank())
                listOfWords[word] = listOfWords.getOrPut(word) { 0 } + 1
        }

        return listOfWords
    }

    private fun readTxtAllWord(file: File): ArrayList<String> {
        val allText = file.readText(charset)
        return splitAndCleanWord(allText)
    }

    fun splitAndCleanWord(allText: String): ArrayList<String> {
        val res = ArrayList<String>()
        if (allText.isNotEmpty()) {
            val splitWordArray = allText.split("\\s+".toRegex()).toTypedArray()
            for (i in splitWordArray.indices) {
                res.add(splitWordArray[i])
            }
        }
        return res
    }

    fun cleanWord(word: String): String {
        var res = word.toLowerCase()
        res = Regex("[-]+$").replace(res, "")
        res = Regex("^[-`]+").replace(res, "")
        if (res.toIntOrNull() == null)
            res = Regex("[^a-z-`]").replace(res, "")
        if (res.length >= 40) return ""
        return res
    }
}
