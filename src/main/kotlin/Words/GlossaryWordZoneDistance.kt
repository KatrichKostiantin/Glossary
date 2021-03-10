package Words

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GlossaryWordZoneDistance(
    val value: String
) {
    val mapFileCount = HashMap<String, LinkedList<Pair<Int, ArrayList<Int>>>>()

    fun processNewFile(fileName: String, wordPosition: LinkedList<Pair<Int, ArrayList<Int>>>) {
        mapFileCount[fileName] = wordPosition
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GlossaryWordZoneDistance) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Words.GlossaryWord(value='$value', mapFileCount=$mapFileCount)"
    }
}