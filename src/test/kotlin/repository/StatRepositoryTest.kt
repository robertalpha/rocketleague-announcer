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



        statRepository.addStatMessage(
            5000L,
            StatMessage(
                "GUID123", "Demolish",
                getPlayerSteam(orange),
                getPlayerEpic(blue)
            ))
        statRepository.addStatMessage(
            6000L,
            StatMessage(
                "GUID123", "Demolish",
                getPlayerSwitch(blue),
                getPlayerSteam(orange)
            ))

        statRepository.addStatMessage(
            7000L,
            StatMessage(
                "OTHER", "Demolish",
                getBot(blue),
                getPlayerPlaystation(orange)
            ))

        val result = statRepository.getStatHistory("GUID123")
        assert(result.size == 2)
        val result2 = statRepository.getStatHistory("GUID123").filter { (timestamp, _) -> timestamp > 5500L}
        assert(result2.size == 1)
    }
}