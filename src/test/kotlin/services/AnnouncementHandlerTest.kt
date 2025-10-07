package nl.vanalphenict.services

import com.janoz.discord.DiscordService
import com.janoz.discord.domain.Activity
import com.janoz.discord.domain.Guild
import com.janoz.discord.domain.VoiceChannel
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dv8tion.jda.api.JDA
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.announcement.AsIs
import nl.vanalphenict.support.getEvent
import nl.vanalphenict.support.getVoiceChannel
import java.util.concurrent.SynchronousQueue
import kotlin.test.Test
import kotlin.time.Instant


class AnnouncementHandlerTest {

    val sampleMapper = constructSampleMapper( mapOf(
        Announcement.AERIAL_GOAL to SampleMapper.AnnouncementWeight(listOf("aerial_goal.wav"), 4),
        Announcement.LONG_GOAL to SampleMapper.AnnouncementWeight(listOf("long_goal.wav"), 3),
        Announcement.HATTRICK to SampleMapper.AnnouncementWeight(listOf("hattrick.wav"), 2)
    ))

    val sampleMapperRev = constructSampleMapper(mapOf(
        Announcement.AERIAL_GOAL to SampleMapper.AnnouncementWeight(listOf("aerial_goal.wav"), 2),
        Announcement.LONG_GOAL to SampleMapper.AnnouncementWeight(listOf("long_goal.wav"), 3),
        Announcement.HATTRICK to SampleMapper.AnnouncementWeight(listOf("hattrick.wav"), 4),
    ))

    val voiceChannel = getVoiceChannel()

    val playedSampleQueue = SynchronousQueue<String>()
    val discordService = MockDiscordService(playedSampleQueue)

    val cut = AnnouncementHandler(
        discordService,
        voiceChannel,
        constructSampleMapper(emptyMap()),
        listOf(
            AsIs(),
            Goal2All()
        )
    )

    @Test
    fun testNoInterpreter() {
        cut.replaceMapping(sampleMapper)

        cut.handleStatMessage(getEvent(Events.DEMOLITION))
        Thread.sleep(150)
        playedSampleQueue.isEmpty() shouldBe true
    }

    @Test
    fun testNoMapping() {
        cut.replaceMapping(sampleMapper)

        cut.handleStatMessage(getEvent(Events.EPIC_SAVE))
        Thread.sleep(150)
        playedSampleQueue.isEmpty() shouldBe true
    }

    @Test
    fun testSingleEvent() {
        cut.replaceMapping(sampleMapper)

        cut.handleStatMessage(getEvent(Events.AERIAL_GOAL))
        playedSampleQueue.take() shouldBe "aerial_goal.wav"
        playedSampleQueue.isEmpty() shouldBe true
    }

    @Test
    fun testMultipleEventsParalel() {
        cut.replaceMapping(sampleMapper)

        cut.handleStatMessage(getEvent(Events.GOAL))
        playedSampleQueue.take() shouldBe "aerial_goal.wav"
        playedSampleQueue.isEmpty() shouldBe true

        cut.replaceMapping(sampleMapperRev)

        cut.handleStatMessage(getEvent(Events.GOAL))
        playedSampleQueue.take() shouldBe "hattrick.wav"
        playedSampleQueue.isEmpty() shouldBe true
    }


    @Test
    fun testMultipleEventsSerial() {
        cut.replaceMapping(sampleMapper)

        cut.handleStatMessage(getEvent(Events.AERIAL_GOAL))
        cut.handleStatMessage(getEvent(Events.LONG_GOAL))
        playedSampleQueue.take() shouldBe "aerial_goal.wav"
        playedSampleQueue.isEmpty() shouldBe true

        cut.handleStatMessage(getEvent(Events.AERIAL_GOAL))
        playedSampleQueue.take() shouldBe "aerial_goal.wav"
        cut.handleStatMessage(getEvent(Events.LONG_GOAL))
        playedSampleQueue.take() shouldBe "long_goal.wav"
        playedSampleQueue.isEmpty() shouldBe true

        cut.replaceMapping(sampleMapperRev)

        cut.handleStatMessage(getEvent(Events.AERIAL_GOAL))
        cut.handleStatMessage(getEvent(Events.LONG_GOAL))
        playedSampleQueue.take() shouldBe "long_goal.wav"
        playedSampleQueue.isEmpty() shouldBe true
    }

    fun constructSampleMapper(mapping : Map<Announcement, SampleMapper.AnnouncementWeight>) : SampleMapper {
        return SampleMapper(
            name = "MOCKED",
            info = "Mocked Samplemapper",
            src = null,
            mapping = mapping
        )
    }

    class Goal2All() : StatToAnnouncment {
        override fun listenTo() = setOf(Events.GOAL)
        override fun interpret(
            statMessage: StatMessage,
            currentTimeStamp: Instant
        ): Set<Announcement> {
            return setOf(Announcement.AERIAL_GOAL,Announcement.LONG_GOAL, Announcement.HATTRICK)
        }
    }

    class MockDiscordService(
        val playedSampleQueue :SynchronousQueue<String>
    ) : DiscordService {

        override fun play(sample: String?, guildId: Long, voiceChannelId: Long) {
            guildId shouldBe 1L
            voiceChannelId shouldBe 2L
            sample shouldNotBe null
            playedSampleQueue.add(sample!!)
        }

        override fun setBotActivity(p0: Activity?) { //do nothing
        }

        override fun getBotActivity(): Activity? = null

        override fun getGuilds(): Collection<Guild?> = emptyList()

        override fun getGuild(p0: Long): Guild? = getGuild(p0)

        override fun getVoiceChannel(p0: Long): VoiceChannel? =
            getVoiceChannel(voiceChannelId = p0)

        override fun connect(p0: Long, p1: Long): Boolean = true

        override fun disconnect(p0: Long) { //do nothing
        }

        override fun play(p0: String?, p1: Long) { //do nothing
        }

        override fun getJda(): JDA? = null
    }
}
