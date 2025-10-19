package nl.vanalphenict.services.announcement

import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.JsonStatMessage
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.parseStatMessage
import nl.vanalphenict.support.getBot
import nl.vanalphenict.support.getOrangeTeam
import nl.vanalphenict.support.getPlayerSteam

class GoalTest {

    val cut = Goal()

    val ts = Instant.parse("2020-08-30T18:43:02Z")

    @Test
    fun testListener() {
        cut.listenTo() shouldContainExactly setOf(StatEvents.GOAL)
    }

    @Test
    fun interpretBotAway() {
        cut.interpret(
            parseStatMessage(
                JsonStatMessage(
                    matchGUID = "123abc",
                    event = "Goal",
                    player = getBot(getOrangeTeam(home = false)),
                )
            )!!,
            ts,
        ) shouldContainExactly
            setOf(Announcement.GOAL, Announcement.GOAL_AWAY, Announcement.GOAL_BY_BOT)
    }

    @Test
    fun interpretBotHome() {
        cut.interpret(
            parseStatMessage(
                JsonStatMessage(
                    matchGUID = "123abc",
                    event = "Goal",
                    player = getBot(getOrangeTeam(home = true)),
                )
            )!!,
            ts,
        ) shouldContainExactly
            setOf(Announcement.GOAL, Announcement.GOAL_HOME, Announcement.GOAL_BY_BOT)
    }

    @Test
    fun interpretPlayerAway() {
        cut.interpret(
            parseStatMessage(
                JsonStatMessage(
                    matchGUID = "123abc",
                    event = "Goal",
                    player = getPlayerSteam(getOrangeTeam(home = false)),
                )
            )!!,
            ts,
        ) shouldContainExactly setOf(Announcement.GOAL, Announcement.GOAL_AWAY)
    }

    @Test
    fun interpretPlayerHome() {
        cut.interpret(
            parseStatMessage(
                JsonStatMessage(
                    matchGUID = "123abc",
                    event = "Goal",
                    player = getPlayerSteam(getOrangeTeam(home = true)),
                )
            )!!,
            ts,
        ) shouldContainExactly setOf(Announcement.GOAL, Announcement.GOAL_HOME)
    }
}
