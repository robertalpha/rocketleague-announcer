package services

import io.kotest.matchers.shouldBe
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.services.SampleMapper
import kotlin.test.BeforeTest
import kotlin.test.Test

class SampleMapperTest {

    val sampleMapper = SampleMapper()

    @BeforeTest
    fun loadMapping() {
        sampleMapper.readSampleMapping({}.javaClass.getResourceAsStream("/mapping.json"))
    }

    @Test
    fun testMapping() {
        sampleMapper.getSample(setOf(
            Announcement.KILL,
            Announcement.KILLED,
            Announcement.COMBO_BREAKER
        )) shouldBe null

        sampleMapper.getSample(setOf(
            Announcement.KILLED,
            Announcement.FIRST_BLOOD
        )) shouldBe "718360|FIRST_BLOOD"
    }
}