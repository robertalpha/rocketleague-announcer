package services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.services.announcement.Retaliation
import nl.vanalphenict.support.blueTeam
import nl.vanalphenict.support.demoMessage
import nl.vanalphenict.support.orangeTeam
import nl.vanalphenict.support.playerEpic
import nl.vanalphenict.support.playerSteam
import nl.vanalphenict.support.playerSwitch

class RetaliationTest {

    @Test
    fun testRetaliation() {
        val cut = Retaliation()
        val homePlayer = playerEpic(blueTeam())
        val otherHomePlayer = playerSwitch(blueTeam())
        val awayPlayer = playerSteam(orangeTeam())

        cut.interpret(
            demoMessage(awayPlayer, homePlayer),
            Instant.parse("2020-08-30T18:43:02Z"),
        ) shouldHaveSize 0
        cut.interpret(
            demoMessage(awayPlayer, homePlayer),
            Instant.parse("2020-08-30T18:43:03Z"),
        ) shouldHaveSize 0
        cut.interpret(
            demoMessage(otherHomePlayer, awayPlayer),
            Instant.parse("2020-08-30T18:43:04Z"),
        ) shouldContain Announcement.RETALIATION
        cut.interpret(
            demoMessage(homePlayer, awayPlayer),
            Instant.parse("2020-08-30T18:43:05Z"),
        ) shouldHaveSize 0
    }
}
