package repository

import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.model.Team
import nl.vanalphenict.repository.StatRepository
import support.getBlueTeam
import support.getBot
import support.getOrangeTeam
import support.getPlayerEpic
import support.getPlayerPlaystation
import support.getPlayerSteam
import support.getPlayerSwitch
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import nl.vanalphenict.utility.TimeUtils.Companion.isOlderThan

class StatRepositoryTest {

    val statRepository: StatRepository = StatRepository()

    @BeforeTest
    fun reset() {
        statRepository.clear()
    }

    @Test
    fun testGetByGuid() {
        val orange: Team = getOrangeTeam()
        val blue: Team = getBlueTeam()


        val first = Instant.parse("2020-01-01T12:00:00Z")
        statRepository.addStatMessage(
            first,
            StatMessage(
                "GUID123", "Demolish",
                getPlayerSteam(orange),
                getPlayerEpic(blue)
            ))
        statRepository.addStatMessage(
            Instant.parse("2020-01-01T12:00:01Z"),
            StatMessage(
                "GUID123", "Demolish",
                getPlayerSwitch(blue),
                getPlayerSteam(orange)
            ))

        statRepository.addStatMessage(
            Instant.parse("2020-01-01T12:00:02Z"),
            StatMessage(
                "OTHER", "Demolish",
                getBot(blue),
                getPlayerPlaystation(orange)
            ))

        val result = statRepository.getStatHistory("GUID123")
        assert(result.size == 2)
        val result2 = statRepository.getStatHistory("GUID123").filter { (timestamp, _) -> timestamp.isOlderThan(first, 500.milliseconds)}
        assert(result2.size == 1)
    }
}