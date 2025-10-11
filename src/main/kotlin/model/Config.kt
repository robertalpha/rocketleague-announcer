package nl.vanalphenict.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MappingConfig(
    val name: String,
    val info: String,
    val mapping: Collection<Mapping>
)

@Serializable
data class Mapping(
    val announcement: Announcement,
    val weight: Int,
    val samples: Collection<String>
)