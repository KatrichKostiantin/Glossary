package BTreeNodes

import GlossaryWordDistance
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class GlossaryNodeDistance(
    val letter: Char,
    var word: GlossaryWordDistance? = null
): GlossaryNodeDistanceRoot() {
    override val childrenNode: HashMap<Char, GlossaryNodeDistance> = HashMap()

    public override fun insert(enterWord: String, index: Int, fileName: String, fileListDistance: LinkedList<Int>) {
        if (index == enterWord.length) {
            if (word == null)
                word = GlossaryWordDistance(enterWord)
            word?.processNewFile(fileName, fileListDistance)
        } else
            super.insert(enterWord, index, fileName, fileListDistance)
    }

    public override fun get(enterWord: String, index: Int): GlossaryWordDistance? {
        if (index == enterWord.length)
            return word
        return super.get(enterWord, index)
    }

    public fun getAll(list: ArrayList<GlossaryWordDistance>): ArrayList<GlossaryWordDistance> {
        word?.let { list.add(it) }
        childrenNode.values.forEach { it.getAll(list) }
        return list
    }
}

open class GlossaryNodeDistanceRoot {
    open val childrenNode: HashMap<Char, GlossaryNodeDistance> = HashMap()

    public open fun insert(enterWord: String, index: Int = 0, fileName: String, fileListDistance: LinkedList<Int>) {
        if (childrenNode[enterWord[index]] == null)
            childrenNode[enterWord[index]] = GlossaryNodeDistance(enterWord[index])
        childrenNode[enterWord[index]]?.insert(enterWord, index + 1, fileName, fileListDistance)

    }

    public open fun get(enterWord: String, index: Int = 0): GlossaryWordDistance? {
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