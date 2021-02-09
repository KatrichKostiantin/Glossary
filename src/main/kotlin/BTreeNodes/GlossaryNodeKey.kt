package BTreeNodes

import GlossaryWord


class GlossaryNodeKey(
    val letter: Char,
    var word: GlossaryWord? = null
) : GlossaryNodeKeyRoot() {
    override val childrenNode: HashMap<Char, GlossaryNodeKey> = HashMap()

    public override fun insert(key: String, enterWord: String, index: Int, fileName: String, fileRepeat: Int) {
        if (index == key.length) {
            if (word == null)
                word = GlossaryWord(enterWord)
            word?.processNewFile(fileName, fileRepeat)
        } else
            super.insert(key, enterWord, index, fileName, fileRepeat)
    }

    public override fun get(enterWord: String, index: Int): GlossaryWord? {
        if (index == enterWord.length)
            return word

        return super.get(enterWord, index)
    }

    public override fun getNode(key: String, index: Int): GlossaryNodeKey? {
        if (index == key.length)
            return this

        return super.getNode(key, index)
    }

    public fun getAll(list: ArrayList<GlossaryWord>): ArrayList<GlossaryWord> {
        word?.let { list.add(it) }
        childrenNode.values.forEach { it.getAll(list) }
        return list
    }
}

open class GlossaryNodeKeyRoot {
    open val childrenNode: HashMap<Char, GlossaryNodeKey> = HashMap()

    public open fun insert(key: String, enterWord: String, index: Int = 0, fileName: String, fileRepeat: Int) {
        if (childrenNode[key[index]] == null)
            childrenNode[key[index]] = GlossaryNodeKey(key[index])
        childrenNode[key[index]]?.insert(key, enterWord, index + 1, fileName, fileRepeat)
    }

    public open fun get(enterWord: String, index: Int = 0): GlossaryWord? {
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

    public open fun getNode(key: String, index: Int): GlossaryNodeKey? {
        if (childrenNode[key[index]] == null)
            return null
        return childrenNode[key[index]]?.getNode(key, index + 1)
    }

    public fun getAllAfter(wordAfter: String): ArrayList<GlossaryWord> {
        val list = ArrayList<GlossaryWord>()
        val nodeAfter = getNode(wordAfter, 0)
        nodeAfter?.getAll(list)
        list.sortBy { it.value }
        return list
    }
}
