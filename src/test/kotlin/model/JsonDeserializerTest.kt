package model

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test
import kotlinx.serialization.json.Json
import nl.vanalphenict.model.JsonClockUpdatedSecondsData
import nl.vanalphenict.model.JsonGoalScoredData
import nl.vanalphenict.model.JsonLogMessage
import nl.vanalphenict.model.JsonMatchGuidData
import nl.vanalphenict.model.JsonStatfeedEventData
import nl.vanalphenict.model.JsonUpdateStateData

class JsonDeserializerTest {

    private val json = Json { ignoreUnknownKeys = true }

    val logMessage =
        """
            {"log":"connected","user":"Janoz","userId":"Steam|1234567890123456|0"}
        """
            .trimIndent()

    val statfeedEventMessage =
        """
            {"MatchGuid":"A1B2C3D4","EventName":"Demolish","Type":"Demolition","MainTarget":{"Name":"PlayerA","Shortcut":1,"TeamNum":0},"SecondaryTarget":{"Name":"PlayerB","Shortcut":2,"TeamNum":1}}
        """
            .trimIndent()

    val statfeedEventNoSecondary =
        """
            {"MatchGuid":"A1B2C3D4","EventName":"Save","Type":"Save","MainTarget":{"Name":"PlayerA","Shortcut":1,"TeamNum":0}}
        """
            .trimIndent()

    val clockUpdatedSecondsMessage =
        """
            {"MatchGuid":"A1B2C3D4","TimeSeconds":180,"bOvertime":false}
        """
            .trimIndent()

    val matchGuidMessage =
        """
            {"MatchGuid":"A1B2C3D4"}
        """
            .trimIndent()

    val updateStateMessage =
        """
            {"MatchGuid":"A1B2C3D4","Players":[{"Name":"PlayerA","PrimaryId":"Steam|123|0","Shortcut":1,"TeamNum":1,"Score":125,"Goals":1,"Shots":2,"Assists":0,"Saves":1,"Touches":14,"CarTouches":3,"Demos":0,"bHasCar":true,"Speed":1200,"Boost":45,"bBoosting":true,"bOnGround":true,"bOnWall":false,"bPowersliding":false,"bDemolished":false,"bSupersonic":true}],"Game":{"Teams":[{"Name":"Blue","TeamNum":0,"Score":1,"ColorPrimary":"0000FF","ColorSecondary":"0000AA"}],"TimeSeconds":180,"bOvertime":false,"Ball":{"Speed":850.5,"TeamNum":0},"bReplay":false,"bHasWinner":false,"Winner":"","Arena":"Stadium_P","bHasTarget":false}}
        """
            .trimIndent()

    val goalScoredMessage =
        """
            {"MatchGuid":"A1B2C3D4","GoalSpeed":87.3,"GoalTime":127.5,"ImpactLocation":{"X":0,"Y":-2944,"Z":320},"Scorer":{"Name":"PlayerA","Shortcut":1,"TeamNum":0},"Assister":{"Name":"PlayerC","Shortcut":3,"TeamNum":0},"BallLastTouch":{"Player":{"Name":"PlayerA","Shortcut":1,"TeamNum":0},"Speed":125}}
        """
            .trimIndent()

    @Test
    fun parseStatfeedEventTest() {
        val output = genericDecode<JsonStatfeedEventData>(statfeedEventMessage)
        output shouldNotBe null
        output!!.eventName shouldBe "Demolish"
        output.secondaryTarget shouldNotBe null
        output.secondaryTarget!!.name shouldBe "PlayerB"
    }

    @Test
    fun parseStatfeedEventNoSecondaryTest() {
        val output = genericDecode<JsonStatfeedEventData>(statfeedEventNoSecondary)
        output shouldNotBe null
        output!!.secondaryTarget shouldBe null
    }

    @Test
    fun parseClockUpdatedSecondsTest() {
        val output = genericDecode<JsonClockUpdatedSecondsData>(clockUpdatedSecondsMessage)
        output shouldNotBe null
        output!!.timeSeconds shouldBe 180
        output.overtime shouldBe false
    }

    @Test
    fun parseMatchGuidTest() {
        val output = genericDecode<JsonMatchGuidData>(matchGuidMessage)
        output shouldNotBe null
        output!!.matchGuid shouldBe "A1B2C3D4"
    }

    @Test
    fun parseUpdateStateTest() {
        val output = genericDecode<JsonUpdateStateData>(updateStateMessage)
        output shouldNotBe null
        output!!.players.size shouldBe 1
        output.players[0].name shouldBe "PlayerA"
        output.game.teams.size shouldBe 1
        output.players[0].teamNum shouldBe 1
        output.players[0].shortcut shouldBe 1
    }

    @Test
    fun parseGoalScoredTest() {
        val output = genericDecode<JsonGoalScoredData>(goalScoredMessage)
        output shouldNotBe null
        output!!.scorer.name shouldBe "PlayerA"
        output.assister shouldNotBe null
        output.assister!!.name shouldBe "PlayerC"
    }

    @Test
    fun parseLogMessageTest() {
        val output = genericDecode<JsonLogMessage>(logMessage)
        output shouldNotBe null
    }

    private inline fun <reified T> genericDecode(input: String): T? {
        return try {
            json.decodeFromString<T>(input)
        } catch (e: Exception) {
            println("could not parse message: $e")
            null
        }
    }
}
