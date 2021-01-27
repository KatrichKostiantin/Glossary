import com.kursx.parser.fb2.Element
import com.kursx.parser.fb2.FictionBook
import com.kursx.parser.fb2.Section
import java.io.File
import java.nio.charset.Charset

class FileReader(
    private val charset: Charset = Charset.defaultCharset()
) {
    public fun readTxtFile(file: File): Map<String, Int> {
        val listOfWords = mutableMapOf<String, Int>()
        val lines: List<String> = file.readLines(charset)
        lines.forEach { line ->
            splitCleanAddLine(line, listOfWords)
        }
        return listOfWords
    }

    fun readFB2File(file: File): Map<String, Int> {
        val listOfWords = mutableMapOf<String, Int>()
        val fictionFile = FictionBook(file)
        fictionFile.body?.sections?.forEach { section: Section? ->
            section?.elements?.forEach { element: Element? ->
                element?.let {
                    splitCleanAddLine(it.text, listOfWords)
                }
            }
        }
        return listOfWords
    }

    public fun splitCleanAddLine(line: String?, listOfWords: MutableMap<String, Int>) {
        if (line != null && line.isNotEmpty()) {
            var splitLineList = line.split("\\s+".toRegex())
            splitLineList = splitLineList.map {
                cleanWord(it)
            }
            splitLineList.forEach { word ->
                if (word.isNotBlank())
                    listOfWords[word] = listOfWords.getOrPut(word) { 0 } + 1
            }
        }
    }

    fun cleanWord(word: String): String {
        var res = word.toLowerCase()
        res = Regex("[-.,]$").replace(res, "")
        res = Regex("^[-.,`]").replace(res, "")
        if (res.toIntOrNull() == null)
            res = Regex("[^a-z-.,`]").replace(res, "")
        return res
    }
}
