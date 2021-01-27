import org.junit.Test
import kotlin.test.assertEquals

internal class ReaderTest {
    var fileReader = FileReader()

    @Test
    fun fileReaderCleanSimpleWordCorrectly() {
        assertEquals("cat", fileReader.cleanWord("Cat"))
        assertEquals("cat", fileReader.cleanWord("cat"))
        assertEquals("cat", fileReader.cleanWord("CAT"))

        assertEquals("", fileReader.cleanWord(""))
    }

    @Test
    fun fileReaderCleanWordWithApostropheCorrectly() {
        assertEquals("cat`s", fileReader.cleanWord("Cat`s"))
        assertEquals("cat`s", fileReader.cleanWord("Cat`S"))
        assertEquals("cats`", fileReader.cleanWord("Cats`"))
        assertEquals("cats`", fileReader.cleanWord("CatS`"))
        assertEquals("cats`", fileReader.cleanWord("CatS`"))

        assertEquals("cat", fileReader.cleanWord("`cat"))

        assertEquals("", fileReader.cleanWord("`"))
    }

    @Test
    fun fileReaderCleanWordWithHyphenCorrectly() {
        assertEquals("zip-lock", fileReader.cleanWord("zip-lock"))
        assertEquals("zip-lock", fileReader.cleanWord("Zip-Lock"))

        assertEquals("zip", fileReader.cleanWord("zip-"))
        assertEquals("zip", fileReader.cleanWord("-Zip"))

        assertEquals("", fileReader.cleanWord("-"))
    }

    @Test
    fun fileReaderCleanWordWithDotCorrectly() {
        assertEquals("cat", fileReader.cleanWord("cat."))
        assertEquals("cat", fileReader.cleanWord(".cat"))

        assertEquals("", fileReader.cleanWord("."))
    }

    @Test
    fun fileReaderCleanWordWithCommaCorrectly() {
        assertEquals("cat", fileReader.cleanWord("cat,"))
        assertEquals("cat", fileReader.cleanWord(",cat"))

        assertEquals("", fileReader.cleanWord(","))
    }

    @Test
    fun fileReaderCleanWordWithNumberCorrectly() {
        assertEquals("agent", fileReader.cleanWord("Agent007"))
    }

    @Test
    fun fileReaderCleanSimpleNumberCorrectly() {
        assertEquals("1919", fileReader.cleanWord("1919"))
    }
}