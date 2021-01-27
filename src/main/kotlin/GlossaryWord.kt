class GlossaryWord(
    val value: String
) {
    val mapFileCount = HashMap<String, Int>()

    fun processNewFile(fileName: String, wordCount: Int) {
        mapFileCount[fileName] = wordCount
    }

    fun allWordCount(): Int {
        var result = 0
        mapFileCount.values.forEach { result += it }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GlossaryWord) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    fun toSaveFormat(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(value).append(":")
        mapFileCount.forEach { entry: Map.Entry<String, Int> ->
            stringBuilder.append(entry.key).append("-").append(entry.value).append(",")
        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)
        return stringBuilder.toString()
    }

    override fun toString(): String {
        return "GlossaryWord(value='$value', mapFileCount=$mapFileCount)"
    }
}