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
        return "GlossaryWord(value='$value', mapFileCount=$mapFileCount)"
    }
}