package nl.vanalphenict.model

import kotlinx.serialization.Serializable

@Serializable
data class MappingConfig(
    val name: String,
    val info: String,
    val src: String? = null,
    val mapping: Collection<Mapping>
)

@Serializable
data class Mapping(
    val announcement: Announcement,
    val weight: Int,
    val samples: Collection<String>
)