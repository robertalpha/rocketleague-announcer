package nl.vanalphenict.services

import java.io.InputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.MappingConfig

class SampleMapper(
    val name: String,
    val info: String,
    private val mapping: Map<Announcement, AnnouncementWeight>,
) {

    data class AnnouncementWeight(val sampleIds: Collection<String>, val weight: Int)

    fun selector(a: Announcement): Int = mapping[a]?.weight ?: -1

    fun getPrevailingAnnouncement(announcements: Set<Announcement>): Announcement? {
        val remaining = announcements.toMutableSet()
        remaining.retainAll(mapping.keys)
        return remaining.maxByOrNull { selector(it) }
    }

    fun getSample(announcement: Announcement): String? {
        return mapping[announcement]?.sampleIds?.random()
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        fun constructSampleMapper(stream: InputStream): SampleMapper {
            val conf: MappingConfig = Json.decodeFromStream(stream)
            val mapping = mutableMapOf<Announcement, AnnouncementWeight>()
            val uniqueWeightValidatorSet = mutableSetOf<Int>()
            conf.mapping.forEach {
                if (uniqueWeightValidatorSet.contains(it.weight)) {
                    throw IllegalArgumentException("Weight ${it.weight} is not unique")
                }
                uniqueWeightValidatorSet.add(it.weight)
                mapping[it.announcement] =
                    AnnouncementWeight(sampleIds = it.samples, weight = it.weight)
            }
            return SampleMapper(conf.name, conf.info, mapping)
        }
    }
}
