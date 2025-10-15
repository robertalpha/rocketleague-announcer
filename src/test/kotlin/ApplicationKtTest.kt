package nl.vanalphenict

import com.janoz.discord.VoiceFactory
import com.janoz.discord.domain.Guild
import com.janoz.discord.domain.VoiceChannel
import integrationTests.AbstractMessagingTest
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import nl.vanalphenict.services.SampleMapper
import nl.vanalphenict.utility.TimeServiceMock

class ApplicationTest : AbstractMessagingTest() {
    @Test
    fun testRoot() = testApplication {
        application {
            val mappedPort = mosquitto.getMappedPort(1883)
            val voiceContext = VoiceFactory.createVoiceContextMock()
            val discordService = voiceContext.discordService
            val configsList = mutableListOf(SampleMapper("123", "123", emptyMap()))
            val voiceChannel = VoiceChannel.builder().guild(Guild.builder().id(1L).build()).id(2L).build()
            moduleWithDependencies(
                discordService = discordService,
                voiceChannel = voiceChannel,
                configs = configsList,
                brokerAddress = "tcp://localhost:$mappedPort",
                TimeServiceMock()
            )
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}