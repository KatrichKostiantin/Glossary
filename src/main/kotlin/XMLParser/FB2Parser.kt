package XMLParser

import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset

class FB2Parser(
    val charset: Charset = Charset.defaultCharset()
) {

    var root: TagTreeNode? = null

    fun read(file: File): TagTreeNode {
        val fileReader = InputStreamReader(file.inputStream(), charset)

        val settings = readTagValue(fileReader)
        val arr = ArrayList(readTagValue(fileReader).split(" "))
        val root = TagTreeNode(arr[0], arr, null)
        root.fillContent(fileReader)
        this.root = root
        return root
    }

    fun getAllContent(): String? {
        return root?.getAllContent()
    }

    fun getBodyContent(): String? {
        return root?.getAllContentFromTag("body")
    }

    fun getTitleContent(): String? {
        return root?.getAllContentFromTag("book-title")
    }

    fun getAuthorContent(): String? {
        return root?.getAllContentFromAllTag("author", true)
    }
}

fun readTagValue(fileReader: InputStreamReader): String {
    val str = StringBuilder()
    var char = ' '
    while (char != '<') char = fileReader.read().toChar()
    char = fileReader.read().toChar()
    while (fileReader.ready() && char != '>') {
        str.append(char)
        char = fileReader.read().toChar()
    }

    return str.toString()
}