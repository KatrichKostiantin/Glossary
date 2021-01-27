import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.charset.Charset

internal class GlossaryCreate {
    @Before
    fun preparation(){
        val file1 = File("src/test/resources/file1.txt")
        val file2 = File("src/test/resources/file2.txt")
        file1.createNewFile()
        file2.createNewFile()

        file1.writeText("I am cat", Charset.defaultCharset())
        file2.writeText("I am dog", Charset.defaultCharset())
    }

    @Test
    fun readingFromFiles(){
        val glossary = Glossary()
        glossary.readAllFiles("src/test/resources/")
        print(glossary)
    }
}