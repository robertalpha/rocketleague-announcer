package nl.vanalphenict.services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.support.blueTeam
import nl.vanalphenict.support.demoMessage
import nl.vanalphenict.support.orangeTeam
import nl.vanalphenict.support.playerEpic
import nl.vanalphenict.support.playerSteam

class MutualDestructionTest {

    @Test
    fun testMutualDestruction_player_first() {
        val cut = MutualDestruction()
        val homePlayer = playerEpic(blueTeam())
        val awayPlayer = playerSteam(orangeTeam())

        // homePlayer demo's first
        cut.interpret(
            demoMessage(homePlayer, awayPlayer),
            Instant.parse("2020-08-30T18:43:00.000Z"),
        ) shouldHaveSize 0
        cut.interpret(
            demoMessage(awayPlayer, homePlayer),
            Instant.parse("2020-08-30T18:43:00.090Z"),
        ) shouldContain Announcement.MUTUAL_DESTRUCTION

        // awayPlayer demo's first
        cut.interpret(
            demoMessage(awayPlayer, homePlayer),
            Instant.parse("2020-08-30T18:46:00.000Z"),
        ) shouldHaveSize 0
        cut.interpret(
            demoMessage(homePlayer, awayPlayer),
            Instant.parse("2020-08-30T18:46:00.090Z"),
        ) shouldContain Announcement.MUTUAL_DESTRUCTION

        // outside of time window
        cut.interpret(
            demoMessage(homePlayer, awayPlayer),
            Instant.parse("2020-08-30T18:50:00.000Z"),
        ) shouldHaveSize 0
        cut.interpret(
            demoMessage(awayPlayer, homePlayer),
            Instant.parse("2020-08-30T18:50:00.590Z"),
        ) shouldHaveSize 0
    }
}
