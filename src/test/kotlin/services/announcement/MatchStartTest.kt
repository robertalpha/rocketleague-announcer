package nl.vanalphenict.services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameEvents
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.repository.GameEventRepository

class MatchStartTest {
    val repo = GameEventRepository()
    val cut = MatchStart(repo)

    @BeforeTest
    fun setUp() {
        repo.clear()
    }

    @Test
    fun testListener() {
        cut.listenTo() shouldContainExactly
            setOf(GameEvents.ROUND_STARTED, GameEvents.COUNTDOWN_BEGIN)
    }

    @Test
    fun interpret() {

        cut.handleGameEvent(
            "2020-08-30T18:40:06Z",
            startCountdownMessage("123"),
            repo,
        ) shouldContain Announcement.COUNTDOWN_START
        cut.handleGameEvent("2020-08-30T18:43:06Z", startRoundMessage("123"), repo)
            .shouldContainAll(Announcement.MATCH_START, Announcement.ROUND_START)
        cut.handleGameEvent("2020-08-30T18:44:26Z", startRoundMessage("123"), repo) shouldContain
            Announcement.ROUND_START
        cut.handleGameEvent("2020-08-30T18:46:26Z", startRoundMessage("123"), repo) shouldContain
            Announcement.ROUND_START
        cut.handleGameEvent("2020-08-30T19:00:00Z", startRoundMessage("222"), repo)
            .shouldContainAll(Announcement.MATCH_START, Announcement.ROUND_START)
        cut.handleGameEvent("2020-08-30T19:02:00Z", startRoundMessage("222"), repo) shouldContain
            Announcement.ROUND_START
    }

    fun startRoundMessage(matchGuid: String) =
        GameEventMessage(matchGuid = matchGuid, gameEvent = GameEvents.ROUND_STARTED)

    fun startCountdownMessage(matchGuid: String) =
        GameEventMessage(matchGuid = matchGuid, gameEvent = GameEvents.COUNTDOWN_BEGIN)

    private fun MatchStart.handleGameEvent(
        ts: String,
        event: GameEventMessage,
        repository: GameEventRepository,
    ): Set<Announcement> {
        val announcements = this.interpret(event, Instant.parse(ts))
        repository.addGameEventMessage(
            Instant.parse(ts),
            event,
            metadata = RLAMetaData(matchGuid = "123", overtime = false, remaining = 180.seconds),
        )
        return announcements
    }
}
