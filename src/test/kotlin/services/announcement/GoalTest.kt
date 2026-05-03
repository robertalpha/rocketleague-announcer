package nl.vanalphenict.services.announcement

import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.support.blueTeam
import nl.vanalphenict.support.botPlayer
import nl.vanalphenict.support.orangeTeam
import nl.vanalphenict.support.playerSteam
import nl.vanalphenict.support.statMessage

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
            statMessage(StatEvents.GOAL, botPlayer(orangeTeam())),
            ts,
        ) shouldContainExactly
            setOf(Announcement.GOAL, Announcement.GOAL_AWAY, Announcement.GOAL_BY_BOT)
    }

    @Test
    fun interpretBotHome() {
        cut.interpret(
            statMessage(StatEvents.GOAL, botPlayer(blueTeam())),
            ts,
        ) shouldContainExactly
            setOf(Announcement.GOAL, Announcement.GOAL_HOME, Announcement.GOAL_BY_BOT)
    }

    @Test
    fun interpretPlayerAway() {
        cut.interpret(
            statMessage(StatEvents.GOAL, playerSteam(orangeTeam())),
            ts,
        ) shouldContainExactly setOf(Announcement.GOAL, Announcement.GOAL_AWAY)
    }

    @Test
    fun interpretPlayerHome() {
        cut.interpret(
            statMessage(StatEvents.GOAL, playerSteam(blueTeam())),
            ts,
        ) shouldContainExactly setOf(Announcement.GOAL, Announcement.GOAL_HOME)
    }
}
