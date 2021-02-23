package BTreeNodes

import GlossaryWordIndex


class GlossaryNode(
    val letter: Char,
    var word: GlossaryWordIndex? = null
) : GlossaryNodeRoot() {
    override val childrenNode: HashMap<Char, GlossaryNode> = HashMap()

    public override fun insert(enterWord: String, index: Int, fileIndex: Long, fileRepeat: Int) {


        if (index == enterWord.length) {
            if (word == null)
                word = GlossaryWordIndex(enterWord)
            word?.processNewFile(fileIndex, fileRepeat)
        } else
            super.insert(enterWord, index, fileIndex, fileRepeat)
    }

    public override fun get(enterWord: String, index: Int): GlossaryWordIndex? {
        if (index == enterWord.length)
            return word

        return super.get(enterWord, index)
    }

    public fun getAll(list: ArrayList<GlossaryWordIndex>): ArrayList<GlossaryWordIndex> {
        word?.let { list.add(it) }
        childrenNode.values.forEach { it.getAll(list) }
        return list
    }

    override fun size(): Long {
        var res = word?.let { 1L } ?: 0L
        childrenNode.values.forEach { res += it.size() }
        return res
    }
}

open class GlossaryNodeRoot {
    open val childrenNode: HashMap<Char, GlossaryNode> = HashMap()

    public open fun insert(enterWord: String, index: Int = 0, fileIndex: Long, fileRepeat: Int) {
        if (childrenNode[enterWord[index]] == null)
            childrenNode[enterWord[index]] = GlossaryNode(enterWord[index])
        childrenNode[enterWord[index]]?.insert(enterWord, index + 1, fileIndex, fileRepeat)
    }

    public open fun get(enterWord: String, index: Int = 0): GlossaryWordIndex? {
        if (childrenNode[enterWord[index]] == null)
            return null
        return childrenNode[enterWord[index]]?.get(enterWord, index + 1)
    }

    public fun getAll(): ArrayList<GlossaryWordIndex> {
        val list = ArrayList<GlossaryWordIndex>()
        childrenNode.values.forEach { it.getAll(list) }
        list.sortBy { it.value }
        return list
    }

    open fun size(): Long {
        var sizeRes = 0L
        childrenNode.values.forEach { sizeRes += it.size() }
        return sizeRes
    }
}
