package nl.vanalphenict.services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameEvents
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.support.getBlueTeam
import nl.vanalphenict.support.getOrangeTeam
import kotlin.test.BeforeTest

class MatchStartTest {
    val repo = GameEventRepository()
    val cut = MatchStart(repo)


    @BeforeTest
    fun setUp() {
        repo.clear()
    }

    @Test
    fun testListener() {
        cut.listenTo() shouldContainExactly setOf(GameEvents.START_ROUND)
    }

    @Test
    fun interpret() {

        cut.handleGameEvent("2020-08-30T18:43:06Z", startRoundMessage("123"), repo) shouldContain Announcement.MATCH_START
        cut.handleGameEvent("2020-08-30T18:44:26Z", startRoundMessage("123"), repo) shouldHaveSize 0
        cut.handleGameEvent("2020-08-30T18:46:26Z", startRoundMessage("123"), repo) shouldHaveSize 0

        cut.handleGameEvent("2020-08-30T19:00:00Z", startRoundMessage("222"), repo) shouldContain Announcement.MATCH_START
        cut.handleGameEvent("2020-08-30T19:02:00Z", startRoundMessage("222"), repo) shouldHaveSize 0
  }

    fun startRoundMessage(matchGUID: String) = GameEventMessage(
        matchGUID = matchGUID,
        gameEvent = GameEvents.START_ROUND.eventName,
        teams = listOf(getBlueTeam(),getOrangeTeam())
    )

    private fun MatchStart.handleGameEvent(ts: String, event: GameEventMessage, repository: GameEventRepository): Set<Announcement> {
        val announcements = this.interpret(event, Instant.parse(ts))
        repository.addGameEventMessage(Instant.parse(ts), event)
        return announcements
    }

}