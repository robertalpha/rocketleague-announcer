package model

import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import nl.vanalphenict.model.GameEventMessageRoot
import nl.vanalphenict.model.TickerMessageRoot
import org.junit.jupiter.api.assertNotNull

class JsonDeserializerTest {

    @Test
    fun gameEventTest() {
        val output = genericDecode<GameEventMessageRoot>(gameEvent)
        assertNotNull(output)
    }

    @Test
    fun tickerTest() {
        val output = genericDecode<TickerMessageRoot>(tickerMessage)
        assertNotNull(output)
    }

    @Test
    fun parseTimestamp() {
        val input = "2025-09-03T09:36:31.006Z"
        val zonedDateTime = ZonedDateTime.parse( input )
        assertEquals(zonedDateTime.hour, 9)
        assertEquals(zonedDateTime.zone, ZoneId.of("Z"))
    }

    private inline fun <reified T> genericDecode(input: String): T? {
        return try {
            Json.decodeFromString<T>(input)
        } catch (e: Exception) {
            println("could not parse message: $e")
            null
        }
    }

    val tickerMessage = """
        { "timestamp": "2025-09-03T09:39:42.306Z", "topic": "rl2mqtt/ticker", "message": { "event": "Assist", "player": { "id": "PS4|888844444444233333|0", "name": "maul", "score": 34, "team": { "clubId": 0, "homeTeam": true, "index": 1, "name": "Team", "num": 1, "primaryColor": { "B": 24, "G": 100, "R": 194 }, "score": 1, "secondaryColor": { "B": 229, "G": 229, "R": 229 } } } } } 
    """.trimIndent()
    val gameEvent = """
        {"timestamp":"2025-09-03T09:36:31.006Z","topic":"rl2mqtt/gameevent","message":{"gameEvent":"Function TAGame.GameEvent_TA.EventPlayerAdded","teams":[{"clubId":0,"homeTeam":true,"index":1,"name":"Team","num":1,"players":[{"club":{"accentColor":{"B":0,"G":178,"R":0},"id":2194253,"name":"JEMOEDER","primaryColor":{"B":38,"G":38,"R":38},"tag":"JM"},"id":"Steam|4444444777777777|0","name":"Janoz","score":0},{"id":"Epic|777777aaaaaaafffffff|0","name":"Luke","score":0}],"primaryColor":{"B":24,"G":100,"R":194},"score":0,"secondaryColor":{"B":229,"G":229,"R":229}},{"players":[{"id":"PS4|88888888883333333|0","name":"arco","score":0}]}]}}
    """.trimIndent()


}