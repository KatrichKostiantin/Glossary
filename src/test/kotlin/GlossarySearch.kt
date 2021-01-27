/*
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.charset.Charset

internal class GlossarySearch {
    @Before
    fun preparation(){
        val file1 = File("src/test/resources/file1.txt")
        val file2 = File("src/test/resources/file2.txt")
        file1.createNewFile()
        file2.createNewFile()

        file1.writeText("katrich kostiantyn dog cat", Charset.defaultCharset())
        file2.writeText("katrich pet cat", Charset.defaultCharset())
    }

    @Test
    fun simpleSearchInFiles() {
        val glossary = Glossary()
        glossary.readAllFiles("src/test/resources/")

        assertArrayEquals(arrayOf("file1.txt", "file2.txt"), glossary.search("katrich")?.toTypedArray())
        assertArrayEquals(arrayOf("file1.txt", ), glossary.search("kostiantyn")?.toTypedArray())
    }

    @Test
    fun searchAndInFiles() {
        val glossary = Glossary()
        glossary.readAllFiles("src/test/resources/")

        assertArrayEquals(arrayOf("file1.txt", "file2.txt"), glossary.search(listOf("katrich"), Glossary.SearchPredicate.AND).toTypedArray())
        assertArrayEquals(arrayOf("file1.txt"), glossary.search(listOf("kostiantyn"), Glossary.SearchPredicate.AND).toTypedArray())

        assertArrayEquals(arrayOf("file1.txt"), glossary.search(listOf("katrich", "kostiantyn"), Glossary.SearchPredicate.AND).toTypedArray())
        assertArrayEquals(arrayOf(), glossary.search(listOf("katrich", "dadwefefe"), Glossary.SearchPredicate.AND).toTypedArray())
    }

    @Test
    fun searchOrInFiles() {
        val glossary = Glossary()
        glossary.readAllFiles("src/test/resources/")

        assertArrayEquals(arrayOf("file1.txt", "file2.txt"), glossary.search(listOf("katrich"), Glossary.SearchPredicate.OR).toTypedArray())
        assertArrayEquals(arrayOf("file1.txt"), glossary.search(listOf("kostiantyn"), Glossary.SearchPredicate.OR).toTypedArray())

        assertArrayEquals(arrayOf("file1.txt", "file2.txt"), glossary.search(listOf("katrich", "kostiantyn"), Glossary.SearchPredicate.OR).toTypedArray())
        assertArrayEquals(arrayOf("file1.txt", "file2.txt"), glossary.search(listOf("katrich", "dadwefefe"), Glossary.SearchPredicate.OR).toTypedArray())
        assertArrayEquals(arrayOf("file1.txt"), glossary.search(listOf("kostiantyn", "dadwefefe"), Glossary.SearchPredicate.OR).toTypedArray())
    }
}*/
