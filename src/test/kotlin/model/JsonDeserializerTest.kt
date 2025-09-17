package model

import kotlin.test.Test
import kotlinx.serialization.json.Json
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.LogMessage
import nl.vanalphenict.model.StatMessage
import kotlin.test.assertNotNull

class JsonDeserializerTest {


    val logMessage = """
        {"log":"connected","user":"Janoz","userId":"Steam|1234567890123456|0"}
    """.trimIndent()

    val gameEventMessage = """
        {"gameEvent":"Function TAGame.GameEvent_TA.EventPlayerRemoved","matchGUID":"C76862A211F08C338A293793E8FBC013","teams":[{"clubId":0,"homeTeam":false,"index":0,"name":"Team","num":0,"players":[{"id":"PS4|1234567890123456789|0","name":"abdo_Elsafah","score":428},{"id":"Epic|3253cb4c335e4f858c32ddebca807573|0","name":"FARE3ON","score":22},{"id":"XboxOne|2535419783021968|0","name":"m7med6855","score":193}],"primaryColor":{"B":255,"G":115,"R":24},"score":2,"secondaryColor":{"B":229,"G":229,"R":229}},{"clubId":0,"homeTeam":true,"index":1,"name":"Team","num":1,"players":[{"club":{"accentColor":{"B":0,"G":178,"R":0},"id":2194253,"name":"JEMOEDER","primaryColor":{"B":38,"G":38,"R":38},"tag":"JM"},"id":"Steam|76561197960757547|0","name":"Janoz","score":100},{"id":"PS4|3330796669522920986|0","name":"TeamGhost-1998","score":46}],"primaryColor":{"B":24,"G":100,"R":194},"score":1,"secondaryColor":{"B":229,"G":229,"R":229}}]}
    """.trimIndent()

    val statMessage = """
        {"event":"Demolish","matchGUID":"C76862A211F08C338A293793E8FBC013","player":{"id":"PS4|6316389444873430622|0","name":"abdo_Elsafah","score":390,"team":{"clubId":0,"homeTeam":false,"index":0,"name":"Team","num":0,"primaryColor":{"B":255,"G":115,"R":24},"score":2,"secondaryColor":{"B":229,"G":229,"R":229}}},"victim":{"id":"PS4|3330796669522920986|0","name":"TeamGhost-1998","score":30,"team":{"clubId":0,"homeTeam":true,"index":1,"name":"Team","num":1,"primaryColor":{"B":24,"G":100,"R":194},"score":1,"secondaryColor":{"B":229,"G":229,"R":229}}}}
    """.trimIndent()

    val gameTimeMessage ="""
        {"matchGUID":"C76862A211F08C338A293793E8FBC013","overtime":false,"remaining":0}
    """.trimIndent()


    @Test
    fun parseStatMessageTest() {
        val output = genericDecode<StatMessage>(statMessage)
        assertNotNull(output)
    }

    @Test
    fun parseGameEventMessageTest() {
        val output = genericDecode<GameEventMessage>(gameEventMessage)
        assertNotNull(output)
    }

    @Test
    fun parseLogMessageTest() {
        val output = genericDecode<LogMessage>(logMessage)
        assertNotNull(output)
    }

    @Test
    fun parseGameTimeMessageTest() {
        val output = genericDecode<GameTimeMessage>(gameTimeMessage)
        assertNotNull(output)
    }

    private inline fun <reified T> genericDecode(input: String): T? {
        return try {
            Json.decodeFromString<T>(input)
        } catch (e: Exception) {
            println("could not parse message: $e")
            null
        }
    }
}