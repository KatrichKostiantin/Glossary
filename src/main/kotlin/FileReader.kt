import java.io.File
import java.nio.charset.Charset
import java.util.*

class FileReader(
    private val charset: Charset = Charset.defaultCharset()
) {
    public fun readTxtFileWithDistance(file: File): Map<String, LinkedList<Int>> {
        val listOfWords = mutableMapOf<String, LinkedList<Int>>()
        val allText = file.readText(charset)
        splitCleanAddToMapWithDistance(allText, listOfWords)

        return listOfWords
    }

    public fun readTxtFilePhrase(file: File): Map<String, Int> {
        val listOfWords = mutableMapOf<String, Int>()
        val allText = file.readText(charset)
        splitCleanAddToMapWith(allText, listOfWords)

        return listOfWords
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

    public fun splitCleanAddToMapWithDistance(allText: String?, listOfWords: MutableMap<String, LinkedList<Int>>) {
        if (allText != null && allText.isNotEmpty()) {
            val splitWordArray = allText.split("\\s+".toRegex()).toTypedArray()
            for (i in splitWordArray.indices) {
                val word = cleanWord(splitWordArray[i])
                if (word.isNotBlank())
                    listOfWords.getOrPut(word) { LinkedList<Int>() }.add(i)
            }
        }
    }

    public fun splitCleanAddToMapWith(allText: String?, listOfWords: MutableMap<String, Int>) {
        if (allText != null && allText.isNotEmpty()) {
            val splitWordArray = allText.split("\\s+".toRegex()).toTypedArray()
            for (i in splitWordArray.indices) {
                splitWordArray[i] = cleanWord(splitWordArray[i])
            }

            for (i in 0 until splitWordArray.size - 1) {
                val word = "${splitWordArray[i]} ${splitWordArray[i + 1]}"
                if (word.isNotBlank())
                    listOfWords[word] = listOfWords.getOrPut(word) { 0 } + 1
            }
        }
    }

    fun cleanWord(word: String): String {
        var res = word.toLowerCase()
        res = Regex("[-]+$").replace(res, "")
        res = Regex("^[-`]+").replace(res, "")
        if (res.toIntOrNull() == null)
            res = Regex("[^a-z-`]").replace(res, "")
        return res
    }
}
