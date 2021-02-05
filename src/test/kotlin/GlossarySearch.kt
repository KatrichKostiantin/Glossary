import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.charset.Charset
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class GlossarySearch {
    @Before
    fun preparation() {
        val file1 = File("src/test/resources/file1.txt")
        val file2 = File("src/test/resources/file2.txt")
        file1.createNewFile()
        file2.createNewFile()

        file1.writeText("katrich kostiantyn dog cat sandora love oreo", Charset.defaultCharset())
        file2.writeText("katrich pet cat", Charset.defaultCharset())
    }

    /* @Test
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
     }*/

    @Test
    fun test(){
        val glossary = Glossary()
        glossary.readAllFiles("src/test/resources/")

        println(glossary.searchPhraseWithDistance("oreo /200 sandora"))
    }

    @Test
    fun searchSimplePhraseWithTwoWord() {
        val glossary = Glossary()
        glossary.readAllFiles("src/test/resources/")

        assertTrue(glossary.searchPhrase("katrich kostiantyn").contains("file1.txt"))
        assertFalse(glossary.searchPhrase("katrich kostiantyn").contains("file2.txt"))

        assertFalse(glossary.searchPhrase("katrich pet").contains("file1.txt"))
        assertTrue(glossary.searchPhrase("katrich pet").contains("file2.txt"))
    }

    @Test
    fun searchSimplePhraseWithThreeWord() {
        val glossary = Glossary()
        glossary.readAllFiles("src/test/resources/")

        assertTrue(glossary.searchPhrase("katrich kostiantyn dog").contains("file1.txt"))
        assertFalse(glossary.searchPhrase("katrich kostiantyn dog").contains("file2.txt"))
    }

    @Test
    fun searchSimplePhraseWithDistance() {
        val glossary = Glossary()
        glossary.readAllFiles("src/test/resources/")

        assertTrue(glossary.searchPhraseWithDistance("katrich /1 kostiantyn /1 dog").contains("file1.txt") )
        assertFalse(glossary.searchPhrase("katrich /1 kostiantyn /1 dog").contains("file2.txt") )


        assertTrue(glossary.searchPhraseWithDistance("katrich /2 dog").contains("file1.txt") )
        assertFalse(glossary.searchPhraseWithDistance("katrich /2 dog").contains("file2.txt") )

        assertTrue(glossary.searchPhraseWithDistance("katrich /3 aaa").isEmpty())
    }
}
