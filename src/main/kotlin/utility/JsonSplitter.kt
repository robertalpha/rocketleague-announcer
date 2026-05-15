package utility

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets

class JsonSplitter {

    companion object {

        fun ByteReadChannel.toJsonSequence(): Sequence<String> =
            InputStreamReader(this.toInputStream(), StandardCharsets.UTF_8).toJsonSequence()

        fun Reader.toJsonSequence(): Sequence<String> = jsonSequence(this)

        /**
         * Split a constant stream of concatenated json objects into a sequence of single json
         * objects. Json objects are not yet parsed but are emitted as string values. If a json
         * object isn't complete before the end of the input, it is never emitted.
         *
         * No buffering, so objects are emitted as soon as they are read completely.
         */
        fun jsonSequence(input: Reader): Sequence<String> = sequence {
            val s = StringBuilder()
            var depth = 0
            var inString = false
            var escapeNext = false
            var current = input.read()
            while (current > -1) {
                val char = current.toChar()
                if (!escapeNext && char == '"') inString = !inString
                escapeNext = !escapeNext && char == '\\'
                if (!inString)
                    when (char) {
                        '{' -> depth++
                        '}' -> depth--
                    }
                s.append(char)
                if (depth == 0) {
                    yield(s.toString())
                    s.clear()
                }
                current = input.read()
            }
        }
    }
}
