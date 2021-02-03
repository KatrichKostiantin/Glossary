import java.util.*
import kotlin.collections.HashMap

class GlossaryNodePhrase(
    val letter: Char,
    var word: GlossaryWord? = null
) {
    val childrenNode: HashMap<Char, GlossaryNodePhrase> = HashMap()

    public fun insert(enterWord: String, index: Int = 0, fileName: String, fileRepeat: Int) {
        if (index == enterWord.length) {
            if (word == null)
                word = GlossaryWord(enterWord)
            word?.processNewFile(fileName, fileRepeat)
        } else {
            if (childrenNode[enterWord[index]] == null)
                childrenNode[enterWord[index]] = GlossaryNodePhrase(enterWord[index])
            childrenNode[enterWord[index]]?.insert(enterWord, index + 1, fileName, fileRepeat)
        }
    }

    public fun get(enterWord: String, index: Int = 0): GlossaryWord? {
        if (index == enterWord.length)
            return word

        if (childrenNode[enterWord[index]] == null)
            return null
        return childrenNode[enterWord[index]]?.get(enterWord, index + 1)
    }
}

class GlossaryNodePhraseRoot {
    val childrenNode: HashMap<Char, GlossaryNodePhrase> = HashMap()

    public fun insert(enterWord: String, index: Int = 0, fileName: String, fileRepeat: Int) {
        if (childrenNode[enterWord[index]] == null)
            childrenNode[enterWord[index]] = GlossaryNodePhrase(enterWord[index])
        childrenNode[enterWord[index]]?.insert(enterWord, index + 1, fileName, fileRepeat)

    }

    public fun get(enterWord: String, index: Int = 0): GlossaryWord? {
        if (childrenNode[enterWord[index]] == null)
            return null
        return childrenNode[enterWord[index]]?.get(enterWord, index + 1)
    }
}

class GlossaryNodeDistance(
    val letter: Char,
    var word: GlossaryWordDistance? = null
) {
    val childrenNode: HashMap<Char, GlossaryNodeDistance> = HashMap()

    public fun insert(enterWord: String, index: Int = 0, fileName: String, fileListDistance: LinkedList<Int>) {
        if (index == enterWord.length) {
            if (word == null)
                word = GlossaryWordDistance(enterWord)
            word?.processNewFile(fileName, fileListDistance)
        } else {
            if (childrenNode[enterWord[index]] == null)
                childrenNode[enterWord[index]] = GlossaryNodeDistance(enterWord[index])
            childrenNode[enterWord[index]]?.insert(enterWord, index + 1, fileName, fileListDistance)
        }
    }

    public fun get(enterWord: String, index: Int = 0): GlossaryWordDistance? {
        if (index == enterWord.length)
            return word

        if (childrenNode[enterWord[index]] == null)
            return null
        return childrenNode[enterWord[index]]?.get(enterWord, index + 1)
    }
}

class GlossaryNodeDistanceRoot {
    val childrenNode: HashMap<Char, GlossaryNodeDistance> = HashMap()

    public fun insert(enterWord: String, index: Int = 0, fileName: String, fileListDistance: LinkedList<Int>) {
        if (childrenNode[enterWord[index]] == null)
            childrenNode[enterWord[index]] = GlossaryNodeDistance(enterWord[index])
        childrenNode[enterWord[index]]?.insert(enterWord, index + 1, fileName, fileListDistance)

    }

    public fun get(enterWord: String, index: Int = 0): GlossaryWordDistance? {
        if (childrenNode[enterWord[index]] == null)
            return null
        return childrenNode[enterWord[index]]?.get(enterWord, index + 1)
    }
}