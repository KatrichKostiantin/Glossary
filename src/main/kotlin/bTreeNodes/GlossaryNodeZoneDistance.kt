package bTreeNodes

import Words.GlossaryWordZoneDistance
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class GlossaryNodeZoneDistance(
    val letter: Char,
    var word: GlossaryWordZoneDistance? = null
) : GlossaryNodeZoneDistanceRoot() {
    override val childrenNode: HashMap<Char, GlossaryNodeZoneDistance> = HashMap()

    override fun insert(
        enterWord: String,
        index: Int,
        fileName: String,
        fileListDistance: LinkedList<Pair<Int, ArrayList<Int>>>
    ) {
        if (index == enterWord.length) {
            if (word == null)
                word = GlossaryWordZoneDistance(enterWord)
            word?.processNewFile(fileName, fileListDistance)
        } else
            super.insert(enterWord, index, fileName, fileListDistance)
    }

    override fun get(enterWord: String, index: Int): GlossaryWordZoneDistance? {
        if (index == enterWord.length)
            return word
        return super.get(enterWord, index)
    }

    fun getAll(list: ArrayList<GlossaryWordZoneDistance>): ArrayList<GlossaryWordZoneDistance> {
        word?.let { list.add(it) }
        childrenNode.values.forEach { it.getAll(list) }
        return list
    }
}

open class GlossaryNodeZoneDistanceRoot {
    open val childrenNode: HashMap<Char, GlossaryNodeZoneDistance> = HashMap()

    open fun insert(
        enterWord: String,
        index: Int = 0,
        fileName: String,
        fileListDistance: LinkedList<Pair<Int, ArrayList<Int>>>
    ) {
        if (childrenNode[enterWord[index]] == null)
            childrenNode[enterWord[index]] = GlossaryNodeZoneDistance(enterWord[index])
        childrenNode[enterWord[index]]?.insert(enterWord, index + 1, fileName, fileListDistance)

    }

    open fun get(enterWord: String, index: Int = 0): GlossaryWordZoneDistance? {
        if (childrenNode[enterWord[index]] == null)
            return null
        return childrenNode[enterWord[index]]?.get(enterWord, index + 1)
    }

    fun getAll(): ArrayList<GlossaryWordZoneDistance> {
        val list = ArrayList<GlossaryWordZoneDistance>()
        childrenNode.values.forEach { it.getAll(list) }
        list.sortBy { it.value }
        return list
    }
}