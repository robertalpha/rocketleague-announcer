package nl.vanalphenict.services

import io.kotest.matchers.shouldBe
import nl.vanalphenict.model.Announcement
import kotlin.test.Test

class SampleMapperTest {

    val sampleMapper = SampleMapper.constructSampleMapper({}.javaClass.getResourceAsStream("/samples/FPS.mapping.json"))


    @Test
    fun TestPrevailingNoMapping() {
        sampleMapper.getPrevailingAnnouncement(setOf(
            Announcement.KILL,
            Announcement.KILLED,
            Announcement.COMBO_BREAKER
        )) shouldBe null
    }

    @Test
    fun TestPrevailingSingleMapping() {
        sampleMapper.getPrevailingAnnouncement(setOf(
            Announcement.KILLED,
            Announcement.FIRST_BLOOD
        )) shouldBe Announcement.FIRST_BLOOD
    }

    @Test
    fun testPrevailingMultipleMapping() {
        sampleMapper.getPrevailingAnnouncement(
            setOf(
                Announcement.KILLED_BY_BOT,
                Announcement.FIRST_BLOOD
            )
        ) shouldBe Announcement.KILLED_BY_BOT
    }

    @Test
    fun testSampleMapping() {
        sampleMapper.getSample(Announcement.FIRST_BLOOD)    shouldBe "718360|FIRST_BLOOD"
        sampleMapper.getSample(Announcement.KILLED_BY_BOT)  shouldBe "718360|HUMILIATION"
        sampleMapper.getSample(Announcement.KILL)           shouldBe null
    }
}