package nl.vanalphenict.services

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.MappingConfig
import java.io.InputStream

class SampleMapper {

    private val mapping = mutableMapOf<Announcement, Pair<Int, Collection<String>>>()

    private fun parseMapping(mappingConfig: MappingConfig) {
        mapping.clear()
        mappingConfig.mapping.forEach {
            mapping[it.announcement] = it.weight to it.samples
        }
    }

    private fun highestScore(one : Announcement, two : Announcement) : Announcement {
        return if (mapping[one]!!.first > mapping[two]!!.first)
            one
        else
            two
    }

    fun getSample(announcements: Set<Announcement>) : String? {
        val remaining = announcements.toMutableSet()
        remaining.retainAll(mapping.keys)
        if (remaining.isEmpty()) return null
        var announcement = remaining.first()
        remaining.forEach { other -> announcement = highestScore(announcement, other) }
        return mapping[announcement]!!.second.random()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun readSampleMapping(stream: InputStream) {
        val conf: MappingConfig = Json.decodeFromStream(stream)
        parseMapping(conf)
    }
}