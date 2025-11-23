package nl.vanalphenict.services

import com.janoz.discord.domain.Guild
import com.janoz.discord.domain.VoiceChannel
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import java.util.concurrent.ArrayBlockingQueue
import kotlin.test.Test

class SamplePlayerTest {
    @Test
    fun playSemiRandom() {
        val vc =
            VoiceChannel.builder()
                .name("123")
                .id(2L)
                .connected(false)
                .guild(Guild.builder().name("name").id(1L).build())
                .build()

        val playedSampleQueue = ArrayBlockingQueue<String>(24)
        val cut = SamplePlayer(AnnouncementHandlerTest.MockDiscordService(playedSampleQueue), vc)

        val sampleIds =
            listOf(
                "pack_1/sample_1.mp3",
                "pack_1/sample_2.mp3",
                "pack_1/sample_3.mp3",
                "pack_1/sample_4.mp3",
                "pack_2/sample_1.mp3",
                "pack_2/sample_2.mp3",
                "pack_2/sample_3.mp3",
                "pack_2/sample_4.mp3",
            )

        repeat(24) { cut.playSemiRandom(sampleIds) }

        // should even out to every sample played three times (order is random / doesn't matter)
        playedSampleQueue shouldContainExactlyInAnyOrder (sampleIds + sampleIds + sampleIds)
    }
}
