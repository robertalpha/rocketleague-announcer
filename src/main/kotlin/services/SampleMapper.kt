package nl.vanalphenict.services

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.MappingConfig
import java.io.InputStream

class SampleMapper(
    val name: String,
    val info: String,
    val src: String?,
    private val mapping: Map<Announcement, Pair<Int, Collection<String>>>) {

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

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        fun constructSampleMapper(stream: InputStream) : SampleMapper{
            val conf: MappingConfig = Json.decodeFromStream(stream)
            val mapping = mutableMapOf<Announcement, Pair<Int, Collection<String>>>()
            conf.mapping.forEach {
                mapping[it.announcement] = it.weight to it.samples
            }
            return SampleMapper(conf.name, conf.info, conf.src, mapping)
        }
    }
}