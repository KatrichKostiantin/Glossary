import java.io.File
import java.nio.charset.Charset
import java.util.*

class FileReader(
    private val charset: Charset = Charset.defaultCharset()
) {
    public fun readTxtFile(file: File): Map<String, LinkedList<Int>> {
        val listOfWords = mutableMapOf<String, LinkedList<Int>>()
        val allText = file.readText(charset)
        splitCleanAddToMap(allText, listOfWords)

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

    public fun splitCleanAddToMap(line: String?, listOfWords: MutableMap<String, LinkedList<Int>>) {
        if (line != null && line.isNotEmpty()) {
            val splitWordArray = line.split("\\s+".toRegex()).toTypedArray()
            for (i in splitWordArray.indices) {
                val word = cleanWord(splitWordArray[i])
                if (word.isNotBlank())
                    listOfWords.getOrPut(word) { LinkedList<Int>() }.add(i)
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
