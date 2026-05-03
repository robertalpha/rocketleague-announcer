package nl.vanalphenict.services.announcement

import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.support.blueTeam
import nl.vanalphenict.support.botPlayer
import nl.vanalphenict.support.orangeTeam
import nl.vanalphenict.support.playerSteam
import nl.vanalphenict.support.statMessage

class ShotTest {

    val cut = Shot()

    val ts = Instant.parse("2020-08-30T18:43:02Z")

    @Test
    fun testListener() {
        cut.listenTo() shouldContainExactly setOf(StatEvents.SHOT)
    }

    @Test
    fun interpretBotAway() {
        cut.interpret(
            statMessage(StatEvents.SHOT, botPlayer(orangeTeam())),
            ts,
        ) shouldContainExactly
            setOf(Announcement.SHOT, Announcement.SHOT_AWAY, Announcement.SHOT_BY_BOT)
    }

    @Test
    fun interpretBotHome() {
        cut.interpret(statMessage(StatEvents.SHOT, botPlayer(blueTeam())), ts) shouldContainExactly
            setOf(Announcement.SHOT, Announcement.SHOT_HOME, Announcement.SHOT_BY_BOT)
    }

    @Test
    fun interpretPlayerAway() {
        cut.interpret(
            statMessage(StatEvents.SHOT, playerSteam(orangeTeam())),
            ts,
        ) shouldContainExactly setOf(Announcement.SHOT, Announcement.SHOT_AWAY)
    }

    @Test
    fun interpretPlayerHome() {
        cut.interpret(
            statMessage(StatEvents.SHOT, playerSteam(blueTeam())),
            ts,
        ) shouldContainExactly setOf(Announcement.SHOT, Announcement.SHOT_HOME)
    }
}
