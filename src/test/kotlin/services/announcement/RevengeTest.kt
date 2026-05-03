package services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.services.announcement.Revenge
import nl.vanalphenict.support.blueTeam
import nl.vanalphenict.support.demoMessage
import nl.vanalphenict.support.orangeTeam
import nl.vanalphenict.support.playerEpic
import nl.vanalphenict.support.playerSteam
import nl.vanalphenict.support.playerSwitch

class RevengeTest {

    @Test
    fun testRevenge() {
        val cut = Revenge()
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
        ) shouldHaveSize 0
        cut.interpret(
            demoMessage(homePlayer, awayPlayer),
            Instant.parse("2020-08-30T18:43:05Z"),
        ) shouldContain Announcement.REVENGE
        cut.interpret(
            demoMessage(homePlayer, awayPlayer),
            Instant.parse("2020-08-30T18:43:06Z"),
        ) shouldHaveSize 0
    }

    @Test
    fun testNotRevenge() {
        val cut = Revenge()
        val homePlayer = playerEpic(blueTeam())
        val awayPlayer = playerSteam(orangeTeam())

        cut.interpret(
            demoMessage(awayPlayer, homePlayer),
            Instant.parse("2020-08-30T18:43:02.000Z"),
        ) shouldHaveSize 0
        // killing the other opponent almost immediately is not a revenge, but happens on mutual
        // destruction
        cut.interpret(
            demoMessage(homePlayer, awayPlayer),
            Instant.parse("2020-08-30T18:43:02.080Z"),
        ) shouldHaveSize 0
        // killing the other opponent after mutual destruction is also not revenge
        cut.interpret(
            demoMessage(homePlayer, awayPlayer),
            Instant.parse("2020-08-30T18:43:03.080Z"),
        ) shouldHaveSize 0
    }
}
