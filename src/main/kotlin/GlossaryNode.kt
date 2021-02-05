import java.util.*
import kotlin.collections.ArrayList
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

    public fun getAll(list: ArrayList<GlossaryWord>): ArrayList<GlossaryWord> {
        word?.let { list.add(it) }
        childrenNode.values.forEach { it.getAll(list) }
        return list
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

    public fun getAll(): ArrayList<GlossaryWord> {
        val list = ArrayList<GlossaryWord>()
        childrenNode.values.forEach { it.getAll(list) }
        list.sortBy { it.value }
        return list
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

    public fun getAll(list: ArrayList<GlossaryWordDistance>): ArrayList<GlossaryWordDistance> {
        word?.let { list.add(it) }
        childrenNode.values.forEach { it.getAll(list) }
        return list
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

    public fun getAll(): ArrayList<GlossaryWordDistance> {
        val list = ArrayList<GlossaryWordDistance>()
        childrenNode.values.forEach { it.getAll(list) }
        list.sortBy { it.value }
        return list
    }
}