import com.kursx.parser.fb2.Element
import com.kursx.parser.fb2.Epigraph
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
            cleanLine(line, listOfWords)
        }
        return listOfWords
    }

    fun readFB2File(file: File): Map<String, Int> {
        val listOfWords = mutableMapOf<String, Int>()
        val fictionFile = FictionBook(file)
        fictionFile.body?.sections?.forEach { section: Section? ->
            section?.elements?.forEach { element: Element? ->
                element?.let {
                    cleanLine(it.text, listOfWords)
                }
            }
        }
        return listOfWords
    }

    private fun cleanLine(line: String?, listOfWords: MutableMap<String, Int>) {
        if (line != null && line.isNotEmpty()) {
            line.split("\\s+".toRegex())
                .map { word ->
                    Regex("[^a-z`'’]").replace(word.toLowerCase(), "")
                }.map { word ->
                    listOfWords[word] = listOfWords.getOrPut(word) { 0 } + 1
                }
        }
    }
}
