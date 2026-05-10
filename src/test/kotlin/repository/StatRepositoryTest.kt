package repository

import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.support.blueTeam
import nl.vanalphenict.support.botPlayer
import nl.vanalphenict.support.demoMessage
import nl.vanalphenict.support.orangeTeam
import nl.vanalphenict.support.playerEpic
import nl.vanalphenict.support.playerPlaystation
import nl.vanalphenict.support.playerSteam
import nl.vanalphenict.support.playerSwitch
import nl.vanalphenict.utility.TimeUtils.Companion.bothHappenWithin

class StatRepositoryTest {

    val statRepository: StatRepository = StatRepository()

    @BeforeTest
    fun reset() {
        statRepository.clear()
    }

    @Test
    fun testGetByGuid() {
        val orange = orangeTeam()
        val blue = blueTeam()

        val first = Instant.parse("2020-01-01T12:00:00Z")
        statRepository.addStatMessage(
            first,
            demoMessage(playerSteam(orange), playerEpic(blue), matchGuid = "GUID123"),
            RLAMetaData(matchGuid = "123", overtime = false, remaining = 100.seconds),
        )
        statRepository.addStatMessage(
            Instant.parse("2020-01-01T12:00:01Z"),
            demoMessage(playerSwitch(blue), playerSteam(orange), matchGuid = "GUID123"),
            RLAMetaData(matchGuid = "123", overtime = false, remaining = 90.seconds),
        )

        statRepository.addStatMessage(
            Instant.parse("2020-01-01T12:00:02Z"),
            demoMessage(botPlayer(blue), playerPlaystation(orange), matchGuid = "OTHER"),
            RLAMetaData(matchGuid = "123", overtime = false, remaining = 80.seconds),
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
