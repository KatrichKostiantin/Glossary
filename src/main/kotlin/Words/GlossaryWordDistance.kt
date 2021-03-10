package Words

class GlossaryWordDistance(
    val value: String
) {
    val mapFileCount = HashMap<String, List<Int>>()

    fun processNewFile(fileName: String, wordPosition: List<Int>) {
        mapFileCount[fileName] = wordPosition
    }

    fun allWordCount(): Int {
        var result = 0
        mapFileCount.values.forEach { result += it.size }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GlossaryWordDistance) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Words.GlossaryWord(value='$value', mapFileCount=$mapFileCount)"
    }

    fun toSaveFormat(): String {
        val str = StringBuilder()
        str.append(value).append(": ")

        mapFileCount.keys.forEach { key ->
            str.append("[").append(key).append(": ")
            mapFileCount[key]?.forEach { str.append(it).append(", ") }
            str.delete(str.length - 3, str.length - 1).append("]")
        }
        return str.toString()
    }
}