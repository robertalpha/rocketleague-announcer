package model

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test
import kotlinx.serialization.json.Json
import nl.vanalphenict.model.JsonGameEventMessage
import nl.vanalphenict.model.JsonGameTimeMessage
import nl.vanalphenict.model.JsonLogMessage
import nl.vanalphenict.model.JsonStatMessage

class JsonDeserializerTest {

    val logMessage =
        """
            {"log":"connected","user":"Janoz","userId":"Steam|1234567890123456|0"}
        """
            .trimIndent()

    val gameEventMessage =
        """
            {"gameEvent":"Function TAGame.GameEvent_TA.EventPlayerRemoved","matchGUID":"C76862A211F08C338A293793E8FBC013","teams":[{"clubId":0,"homeTeam":false,"index":0,"name":"Team","num":0,"players":[{"id":"PS4|1234567890123456789|0","name":"abdo_Elsafah","score":428},{"id":"Epic|3253cb4c335e4f858c32ddebca807573|0","name":"FARE3ON","score":22},{"id":"XboxOne|2535419783021968|0","name":"m7med6855","score":193}],"primaryColor":{"B":255,"G":115,"R":24},"score":2,"secondaryColor":{"B":229,"G":229,"R":229}},{"clubId":0,"homeTeam":true,"index":1,"name":"Team","num":1,"players":[{"club":{"accentColor":{"B":0,"G":178,"R":0},"id":2194253,"name":"JEMOEDER","primaryColor":{"B":38,"G":38,"R":38},"tag":"JM"},"id":"Steam|76561197960757547|0","name":"Janoz","score":100},{"id":"PS4|3330796669522920986|0","name":"TeamGhost-1998","score":46}],"primaryColor":{"B":24,"G":100,"R":194},"score":1,"secondaryColor":{"B":229,"G":229,"R":229}}]}
        """
            .trimIndent()

    val gameEventStartMessage =
        """
        {"gameEvent":"Function TAGame.GameEvent_TA.EventPlayerAdded","matchGUID":"","teams":[{"players":[{"club":{"accentColor":{"B":0,"G":178,"R":0},"id":2194253,"name":"JEMOEDER","primaryColor":{"B":38,"G":38,"R":38},"tag":"JM"},"id":"Steam|76561197963432159|0","name":"Robert","score":0}]}]}
         """
            .trimIndent()

    val gameEventNullTeamsMessage =
        """
            {"gameEvent":"Function TAGame.GameEvent_Soccar_TA.OnAllTeamsCreated","matchGUID":"","teams":null}
        """
            .trimIndent()

    val statMessage =
        """
            {"event":"Demolish","matchGUID":"C76862A211F08C338A293793E8FBC013","player":{"id":"PS4|6316389444873430622|0","name":"abdo_Elsafah","score":390,"team":{"clubId":0,"homeTeam":false,"index":0,"name":"Team","num":0,"primaryColor":{"B":255,"G":115,"R":24},"score":2,"secondaryColor":{"B":229,"G":229,"R":229}}},"victim":{"id":"PS4|3330796669522920986|0","name":"TeamGhost-1998","score":30,"team":{"clubId":0,"homeTeam":true,"index":1,"name":"Team","num":1,"primaryColor":{"B":24,"G":100,"R":194},"score":1,"secondaryColor":{"B":229,"G":229,"R":229}}}}
        """
            .trimIndent()

    val statMessageWithNulls =
        """
            {"event":"Demolish","matchGUID":"C76862A211F08C338A293793E8FBC013","player":{"id":"PS4|6316389444873430622|0","name":"abdo_Elsafah","score":390,"team":{"clubId":0,"homeTeam":false,"index":0,"num":0,"primaryColor":{"B":255,"G":115,"R":24},"score":2,"secondaryColor":{"B":229,"G":229,"R":229}}},"victim":{"id":"PS4|3330796669522920986|0","name":"TeamGhost-1998","score":30,"team":{"clubId":0,"homeTeam":true,"index":1,"name":"Team","num":1,"primaryColor":{"B":24,"G":100,"R":194},"score":1,"secondaryColor":{"B":229,"G":229,"R":229}}}}
        """
            .trimIndent()

    val gameTimeMessage =
        """
            {"matchGUID":"C76862A211F08C338A293793E8FBC013","overtime":false,"remaining":0}
        """
            .trimIndent()

    @Test
    fun parseStatMessageTest() {
        val output = genericDecode<JsonStatMessage>(statMessage)
        output shouldNotBe null
    }

    @Test
    fun parseStatMessageTeamNullTest() {
        val output = genericDecode<JsonStatMessage>(statMessageWithNulls)
        output shouldNotBe null
        val team = output?.player?.team!!

        team.name shouldBe ""
        team.players shouldBe emptyList()
    }

    @Test
    fun parseGameEventMessageTest() {
        val output = genericDecode<JsonGameEventMessage>(gameEventMessage)
        output shouldNotBe null
    }

    @Test
    fun parseGameEventStartMessageTest() {
        val output = genericDecode<JsonGameEventMessage>(gameEventStartMessage)
        output shouldNotBe null
    }

    @Test
    fun parseGameEventNullTeamsMessageTest() {
        val output = genericDecode<JsonGameEventMessage>(gameEventNullTeamsMessage)
        output shouldNotBe null
    }

    @Test
    fun parseLogMessageTest() {
        val output = genericDecode<JsonLogMessage>(logMessage)
        output shouldNotBe null
    }

    @Test
    fun parseGameTimeMessageTest() {
        val output = genericDecode<JsonGameTimeMessage>(gameTimeMessage)
        output shouldNotBe null
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
