package repository

import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.JsonStatMessage
import nl.vanalphenict.model.JsonTeam
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.parseStatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.support.getBlueTeam
import nl.vanalphenict.support.getBot
import nl.vanalphenict.support.getOrangeTeam
import nl.vanalphenict.support.getPlayerEpic
import nl.vanalphenict.support.getPlayerPlaystation
import nl.vanalphenict.support.getPlayerSteam
import nl.vanalphenict.support.getPlayerSwitch
import nl.vanalphenict.utility.TimeUtils.Companion.bothHappenWithin

class StatRepositoryTest {

    val statRepository: StatRepository = StatRepository()

    @BeforeTest
    fun reset() {
        statRepository.clear()
    }

    @Test
    fun testGetByGuid() {
        val orange: JsonTeam = getOrangeTeam()
        val blue: JsonTeam = getBlueTeam()

        val first = Instant.parse("2020-01-01T12:00:00Z")
        statRepository.addStatMessage(
            first,
            parseStatMessage(
                JsonStatMessage("GUID123", "Demolish", getPlayerSteam(orange), getPlayerEpic(blue))
            )!!,
            RLAMetaData(matchGUID = "123", overtime = false, remaining = 100.seconds),
        )
        statRepository.addStatMessage(
            Instant.parse("2020-01-01T12:00:01Z"),
            parseStatMessage(
                JsonStatMessage(
                    "GUID123",
                    "Demolish",
                    getPlayerSwitch(blue),
                    getPlayerSteam(orange),
                )
            )!!,
            RLAMetaData(matchGUID = "123", overtime = false, remaining = 90.seconds),
        )

        statRepository.addStatMessage(
            Instant.parse("2020-01-01T12:00:02Z"),
            parseStatMessage(
                JsonStatMessage("OTHER", "Demolish", getBot(blue), getPlayerPlaystation(orange))
            )!!,
            RLAMetaData(matchGUID = "123", overtime = false, remaining = 80.seconds),
        )

        val result = statRepository.getStatHistory("GUID123")
        result.size shouldBe 2
        val result2 =
            statRepository.getStatHistory("GUID123").filter { (timestamp, _) ->
                timestamp.bothHappenWithin(first, 500.milliseconds)
            }
        result2.size shouldBe 1
    }
}
