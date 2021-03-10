import bTreeNodes.GlossaryNodeRoot
import Words.GlossaryWordIndex
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GlossarySPIMI(
    private val charset: Charset = Charset.defaultCharset(),
) {
    val fileReader = FileReaderCustom(charset)
    private val mapFileIndex = ArrayList<String>()
    private var rootGlossaryNodeSPIMI = GlossaryNodeRoot()
    private val oneRootSize = 200000 //1gb

    fun SPIMI(folderPath: String = "src/main/resources/texts/") {
        var flagHasMoreFile: Boolean = true
        var fileIterator = 0
        while (flagHasMoreFile) {
            flagHasMoreFile = false
            File(folderPath).walk().forEach { file ->
                if (file.isFile) {
                    if (!mapFileIndex.contains(file.canonicalPath)) {
                        readAllFiles(folderPath)
                        saveGlossary("GlossaryV$fileIterator")
                        fileIterator++
                        flagHasMoreFile = true
                    }
                }
            }
        }
        margeAllBlocks()
        saveFileIndexes()
    }

    public fun margeAllBlocks(folderPath: String = "../GlossaryFolder/") {
        val readerBufferList: ArrayList<BufferedReader> = ArrayList()
        val map = HashMap<BufferedReader, GlossaryWordIndex>()
        try {
            File(folderPath).walk().forEach { file ->
                if (file.isFile && file.name != "Glossary.txt" && file.name != "FileIndexes.txt")
                    readerBufferList.add(file.bufferedReader(charset))
            }
            val glossaryFile = File(folderPath + "Glossary.txt")
            glossaryFile.createNewFile()
            glossaryFile.writeText("")
            readerBufferList.forEach { fileReader -> map.put(fileReader, convertLineToGlossaryWord(fileReader.readLine()!!)) }
            val list = ArrayList<BufferedReader>()
            while (map.isNotEmpty()) {
                val minWord: GlossaryWordIndex = map.values.minByOrNull { glossaryWordIndex -> glossaryWordIndex.value }!!
                for (key in map.keys) {
                    if (map[key]!! == minWord) {
                        minWord.marge(map[key]!!)
                        if (key.ready()) map[key] = convertLineToGlossaryWord(key.readLine())
                        else list.add(key)
                    }
                }
                list.forEach { bufferedReader ->
                    if (map.containsKey(bufferedReader)) map.remove(bufferedReader)
                }
                glossaryFile.appendText(minWord.toSaveFormat() + "\n")
            }

        } catch (e: IOException) {
            println(e)
        } finally {
            readerBufferList.forEach { fileReader -> fileReader.close() }
        }
    }

    private fun convertLineToGlossaryWord(line: String): GlossaryWordIndex {
        val pair = line.split(":")
        val res = GlossaryWordIndex(pair[0])
        val files = pair[1].split(",")
        files.forEach { fileLine ->
            val fileCount = fileLine.split("-")
            res.processNewFile(fileCount[0].toLong(), fileCount[1].toInt())
        }
        return res
    }

    public fun saveFileIndexes(fileDirectory: String = "../GlossaryFolder/") {
        val file = File(fileDirectory + "FileIndexes.txt")
        if (!file.exists()) file.createNewFile()
        file.writeText(Arrays.toString(mapFileIndex.toArray()), charset)
    }

    fun readAllFiles(folderPath: String = "src/main/resources/texts/") {
        println("Start reading from directory (path: $folderPath)")
        File(folderPath).walk().forEach { file ->
            when {
                rootGlossaryNodeSPIMI.size() >= oneRootSize -> return
                file.isDirectory -> {
                    //                    println("File with path ${file.name} is directory")
                }
                mapFileIndex.contains(file.canonicalPath) -> {
                    println("File with path ${file.canonicalPath} already in directory")
                }
                else -> {
                    println("freeMemory/totalMemory = ${Runtime.getRuntime().freeMemory()}/${Runtime.getRuntime().totalMemory()}")
                    val fileExtension = FileExtension.valueOf(file.name.substringAfterLast("."))
                    when (fileExtension) {
                        FileExtension.txt -> {
                            readAllWordFromTxtFile(file, mapFileIndex.size.toLong())
                            mapFileIndex.add(file.canonicalPath)
                        }
                        //FileExtension.fb2 -> readAllWordsFromFB2File(file)
                        else -> println("File with extension $this not supported")
                    }
                }
            }
        }
    }

    private fun readAllWordFromTxtFile(file: File, fileIndex: Long) {
        val newWords = fileReader.readTxtFileWord(file)
        newWords.keys.forEach { word ->
            newWords[word]?.let { rootGlossaryNodeSPIMI.insert(word, fileIndex = fileIndex, fileRepeat = it) }
        }
        //        println("Read file ${file.name}")
    }

    public fun saveGlossary(fileName: String, fileDirectory: String = "../GlossaryFolder/") {
        val file = File("$fileDirectory$fileName.txt")
        if (!file.exists()) file.createNewFile()
        val stringBuilder = StringBuilder()
        val list = rootGlossaryNodeSPIMI.getAll()
        rootGlossaryNodeSPIMI = GlossaryNodeRoot()
        list.forEach { glossaryWordDistance ->
            stringBuilder.append(glossaryWordDistance.toSaveFormat()).append("\n")
        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)
        file.writeText(stringBuilder.toString(), charset)
    }
}