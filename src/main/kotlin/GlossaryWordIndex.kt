class GlossaryWordIndex(
    val value: String
) {
    val mapFileCount = HashMap<Long, Int>()

    fun processNewFile(fileIndex: Long, wordCount: Int) {
        mapFileCount[fileIndex] = wordCount
    }

    fun allWordCount(): Int {
        var result = 0
        mapFileCount.values.forEach { result += it }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GlossaryWordIndex) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    fun toSaveFormat(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(value).append(":")
        mapFileCount.forEach { entry: Map.Entry<Long, Int> ->
            stringBuilder.append(entry.key).append("-").append(entry.value).append(",")
        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)
        return stringBuilder.toString()
    }

    fun marge(glossary: GlossaryWordIndex) {
        if(mapFileCount == glossary.mapFileCount) return
        glossary.mapFileCount.forEach { entry: Map.Entry<Long, Int> ->
            if (mapFileCount.containsKey(entry.key)) mapFileCount[entry.key] = mapFileCount[entry.key]!! + entry.value
            else mapFileCount[entry.key] = entry.value
        }
    }
}