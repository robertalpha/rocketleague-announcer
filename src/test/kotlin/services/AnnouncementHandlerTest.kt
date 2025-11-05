package nl.vanalphenict.services

import com.janoz.discord.DiscordService
import com.janoz.discord.domain.Activity
import com.janoz.discord.domain.Guild
import com.janoz.discord.domain.VoiceChannel
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import kotlin.test.Test
import kotlin.time.Instant
import net.dv8tion.jda.api.JDA
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.announcement.AsIs
import nl.vanalphenict.support.getEvent
import nl.vanalphenict.support.getMetaData
import nl.vanalphenict.support.getVoiceChannel

class AnnouncementHandlerTest {

    val sampleMapper =
        constructSampleMapper(
            mapOf(
                Announcement.AERIAL_GOAL to
                    SampleMapper.AnnouncementWeight(listOf("aerial_goal.wav"), 4),
                Announcement.LONG_GOAL to
                    SampleMapper.AnnouncementWeight(listOf("long_goal.wav"), 3),
                Announcement.HATTRICK to SampleMapper.AnnouncementWeight(listOf("hattrick.wav"), 2),
            )
        )

    val sampleMapperRev =
        constructSampleMapper(
            mapOf(
                Announcement.AERIAL_GOAL to
                    SampleMapper.AnnouncementWeight(listOf("aerial_goal.wav"), 2),
                Announcement.LONG_GOAL to
                    SampleMapper.AnnouncementWeight(listOf("long_goal.wav"), 3),
                Announcement.HATTRICK to SampleMapper.AnnouncementWeight(listOf("hattrick.wav"), 4),
            )
        )

    val playedSampleQueue = ArrayBlockingQueue<String>(10)

    val cut =
        AnnouncementHandler(
            MockDiscordService(playedSampleQueue),
            getVoiceChannel(),
            constructSampleMapper(emptyMap()),
            listOf(AsIs(), Save2All()),
            gameEventInterpreters = listOf(),
        )

    @Test
    fun testNoInterpreter() {
        cut.replaceMapping(sampleMapper)

        cut.handleStatMessage(getEvent(StatEvents.DEMOLITION), getMetaData())
        Thread.sleep(150)
        playedSampleQueue.isEmpty() shouldBe true
    }

    @Test
    fun testNoMapping() {
        cut.replaceMapping(sampleMapper)

        cut.handleStatMessage(getEvent(StatEvents.EPIC_SAVE), getMetaData())
        Thread.sleep(150)
        playedSampleQueue.isEmpty() shouldBe true
    }

    @Test
    fun testSingleEvent() {
        cut.replaceMapping(sampleMapper)

        cut.handleStatMessage(getEvent(StatEvents.AERIAL_GOAL), getMetaData())
        playedSampleQueue.take() shouldBe "aerial_goal.wav"
        playedSampleQueue.isEmpty() shouldBe true
    }

    @Test
    fun testMultipleEventsParalel() {
        cut.replaceMapping(sampleMapper)

        cut.handleStatMessage(getEvent(StatEvents.SAVE), getMetaData())
        playedSampleQueue.take() shouldBe "aerial_goal.wav"
        playedSampleQueue.isEmpty() shouldBe true

        cut.replaceMapping(sampleMapperRev)

        cut.handleStatMessage(getEvent(StatEvents.SAVE), getMetaData())
        playedSampleQueue.take() shouldBe "hattrick.wav"
        playedSampleQueue.isEmpty() shouldBe true
    }

    @Test
    fun testMultipleEventsSerial() {
        cut.replaceMapping(sampleMapper)

        cut.handleStatMessage(getEvent(StatEvents.AERIAL_GOAL), getMetaData())
        cut.handleStatMessage(getEvent(StatEvents.LONG_GOAL), getMetaData())
        playedSampleQueue.take() shouldBe "aerial_goal.wav"
        playedSampleQueue.isEmpty() shouldBe true

        cut.handleStatMessage(getEvent(StatEvents.AERIAL_GOAL), getMetaData())
        Thread.sleep(110)
        cut.handleStatMessage(getEvent(StatEvents.LONG_GOAL), getMetaData())
        playedSampleQueue.take() shouldBe "aerial_goal.wav"
        playedSampleQueue.take() shouldBe "long_goal.wav"
        playedSampleQueue.isEmpty() shouldBe true

        cut.replaceMapping(sampleMapperRev)

        cut.handleStatMessage(getEvent(StatEvents.AERIAL_GOAL), getMetaData())
        cut.handleStatMessage(getEvent(StatEvents.LONG_GOAL), getMetaData())
        playedSampleQueue.take() shouldBe "long_goal.wav"
        playedSampleQueue.isEmpty() shouldBe true
    }

    fun constructSampleMapper(
        mapping: Map<Announcement, SampleMapper.AnnouncementWeight>
    ): SampleMapper {
        return SampleMapper(name = "MOCKED", info = "Mocked Samplemapper", mapping = mapping)
    }

    class Save2All() : StatToAnnouncment {
        override fun listenTo() = setOf(StatEvents.SAVE)

        override fun interpret(
            statMessage: StatMessage,
            currentTimeStamp: Instant,
        ): Set<Announcement> {
            return setOf(Announcement.AERIAL_GOAL, Announcement.LONG_GOAL, Announcement.HATTRICK)
        }
    }

    class MockDiscordService(val playedSampleQueue: BlockingQueue<String>) : DiscordService {

        override fun play(sample: String?, guildId: Long, voiceChannelId: Long) {
            guildId shouldBe 1L
            voiceChannelId shouldBe 2L
            sample shouldNotBe null
            playedSampleQueue.add(sample!!)
        }

        override fun setBotActivity(p0: Activity?) { // do nothing
        }

        override fun getBotActivity(): Activity? = null

        override fun getGuilds(): Collection<Guild?> = emptyList()

        override fun getGuild(p0: Long): Guild? = getGuild(p0)

        override fun getVoiceChannel(p0: Long): VoiceChannel? = getVoiceChannel(voiceChannelId = p0)

        override fun connect(p0: Long, p1: Long): Boolean = true

        override fun disconnect(p0: Long) { // do nothing
        }

        override fun play(p0: String?, p1: Long) { // do nothing
        }

        override fun getJda(): JDA? = null
    }
}
