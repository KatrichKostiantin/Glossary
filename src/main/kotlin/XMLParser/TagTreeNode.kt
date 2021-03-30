package XMLParser

import java.io.InputStreamReader

class TagTreeNode(
    public val tag: String,
    val param: ArrayList<String>,
    val father: TagTreeNode?
) {
    var content: String? = null
    var children: ArrayList<TagTreeNode?> = ArrayList(0)

    fun fillContent(fileReader: InputStreamReader) {
        var flag = true
        val str = StringBuilder()
        while (fileReader.ready()) {
            val char = fileReader.read().toChar()

            if (flag) {
                if (char != '\n' && char != ' ')
                    flag = false
                else
                    continue
            }

            if (char == '<') {
                val tag = getTagValue(fileReader)
                if (checkCloseTag(tag)) {
                    if (str.isNotEmpty())
                        content = str.toString()
                    return
                }
                createChildren(tag, fileReader)
                flag = true
            } else
                str.append(char)
        }
    }

    private fun getTagValue(fileReader: InputStreamReader): String {
        val str = StringBuilder()
        var char = fileReader.read().toChar()
        while (char != '>') {
            str.append(char)
            char = fileReader.read().toChar()
        }
        return str.toString()
    }

    private fun checkCloseTag(textTag: String): Boolean {
        val array = ArrayList(textTag.split(" "))
        return array[0] == "/$tag"
    }

    private fun createChildren(tagChildren: String, fileReader: InputStreamReader) {
        val array = ArrayList(tagChildren.split(" "))
        val tagNode = TagTreeNode(array[0], array, this)
        children.add(tagNode)
        if (tagChildren[tagChildren.length - 1] != '/')
            tagNode.fillContent(fileReader)
    }

    fun getAllContent(addSpace: Boolean = false): String? {
        val str = StringBuilder()
        content?.let {
            str.append(it)
            if (addSpace)
                str.append(" ")
        }
        children.forEach {
            str.append(it?.getAllContent(addSpace))
        }
        return str.toString()
    }

    fun getAllContentFromTag(searchTag: String, addSpace: Boolean = false): String? {
        if (tag == searchTag)
            return this.getAllContent(addSpace)
        else {
            children.forEach {
                it?.getAllContentFromTag(searchTag, addSpace)?.let { s ->
                    return s
                }
            }
            return null
        }
    }

    fun getAllContentFromAllTag(searchTag: String, addSpace: Boolean = false): String {
        val str = StringBuilder()
        if (tag == searchTag)
            str.append(this.getAllContent(addSpace))
        else {
            children.forEach {
                it?.getAllContentFromAllTag(searchTag, addSpace)?.let { s ->
                    str.append(s)
                }
            }
        }
        return str.toString()
    }
}