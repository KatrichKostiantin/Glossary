package BTreeNodes

import GlossaryWord


class GlossaryNodePhrase(
    val letter: Char,
    var word: GlossaryWord? = null
): GlossaryNodePhraseRoot() {
    override val childrenNode: HashMap<Char, GlossaryNodePhrase> = HashMap()

    public override fun insert(enterWord: String, index: Int, fileName: String, fileRepeat: Int) {



        if (index == enterWord.length) {
            if (word == null)
                word = GlossaryWord(enterWord)
            word?.processNewFile(fileName, fileRepeat)
        } else
            super.insert(enterWord, index, fileName, fileRepeat)
    }

    public override fun get(enterWord: String, index: Int): GlossaryWord? {
        if (index == enterWord.length)
            return word

        return super.get(enterWord, index)
    }

    public fun getAll(list: ArrayList<GlossaryWord>): ArrayList<GlossaryWord> {
        word?.let { list.add(it) }
        childrenNode.values.forEach { it.getAll(list) }
        return list
    }
}

open class GlossaryNodePhraseRoot {
    open val childrenNode: HashMap<Char, GlossaryNodePhrase> = HashMap()

    public open fun insert(enterWord: String, index: Int = 0, fileName: String, fileRepeat: Int) {
        if (childrenNode[enterWord[index]] == null)
            childrenNode[enterWord[index]] = GlossaryNodePhrase(enterWord[index])
        childrenNode[enterWord[index]]?.insert(enterWord, index + 1, fileName, fileRepeat)
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
}
