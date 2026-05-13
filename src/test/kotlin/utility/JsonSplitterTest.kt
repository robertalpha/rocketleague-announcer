package nl.vanalphenict.utility

import io.kotest.matchers.collections.shouldContainExactly
import java.io.StringReader
import kotlin.test.Test
import utility.JsonSplitter.Companion.jsonSequence

class JsonSplitterTest {

    @Test
    fun testNormal() {
        splitterToList("""{"a":1,"b":2}{"c":3,"d":4}""") shouldContainExactly
            listOf("{\"a\":1,\"b\":2}", "{\"c\":3,\"d\":4}")
    }

    @Test
    fun testCurlyBrace() {
        splitterToList("""{"a}":1,"b":2}{"c":3,"d":4}""") shouldContainExactly
            listOf("{\"a}\":1,\"b\":2}", "{\"c\":3,\"d\":4}")
    }

    @Test
    fun tetEarlyEnding() {
        splitterToList("""{"a}":1,"b":2}{"c":3,"d":4""") shouldContainExactly
            listOf("{\"a}\":1,\"b\":2}")
    }

    @Test
    fun testnested() {
        splitterToList("""{"a}":1,"b":{"c":3,"d":4}}""") shouldContainExactly
            listOf("{\"a}\":1,\"b\":{\"c\":3,\"d\":4}}")
    }

    @Test
    fun testEscapedQuote() {
        splitterToList("""{"a}":"\"","b":{"c":3,"d":4}}""") shouldContainExactly
            listOf("{\"a}\":\"\\\"\",\"b\":{\"c\":3,\"d\":4}}")
    }

    private fun splitterToList(input: String): List<String> =
        jsonSequence(StringReader(input)).toList()
}
