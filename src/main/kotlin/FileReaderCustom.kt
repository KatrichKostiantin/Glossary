import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

class FileReaderCustom(
    private val charset: Charset = Charset.defaultCharset()
) {
    public fun readTxtFileWithDistance(file: File): Map<String, LinkedList<Int>> {
        val listOfWords = mutableMapOf<String, LinkedList<Int>>()
        val splitWordArray = readTxtAllWord(file)

        for (i in splitWordArray.indices) {
            val word = cleanWord(splitWordArray[i])
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

    /*fun readFB2File(file: File): Map<String, Int> {
        val listOfWords = mutableMapOf<String, Int>()
        val fictionFile = FictionBook(file)
        fictionFile.body?.sections?.forEach { section: Section? ->
            section?.elements?.forEach { element: Element? ->
                element?.let {
                    splitCleanAddToMap(it.text, listOfWords)
                }
            }
        }
        return listOfWords
    }*/

    public fun splitAndCleanWord(allText: String): ArrayList<String> {
        val res = ArrayList<String>()
        if (allText.isNotEmpty()) {
            val splitWordArray = allText.split("\\s+".toRegex()).toTypedArray()
            for (i in splitWordArray.indices) {
                val word = cleanWord(splitWordArray[i])
                res.add(word)
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
        if(res.length >= 40) return ""
        return res
    }
}
