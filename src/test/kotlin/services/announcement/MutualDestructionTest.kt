package nl.vanalphenict.services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.JsonPlayer
import nl.vanalphenict.model.JsonStatMessage
import nl.vanalphenict.model.Player
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.model.parseStatMessage
import nl.vanalphenict.support.getBlueTeam
import nl.vanalphenict.support.getOrangeTeam
import nl.vanalphenict.support.getPlayerEpic
import nl.vanalphenict.support.getPlayerSteam

class MutualDestructionTest {


    @Test
    fun testMutualDestruction_player_first() {
        val cut = MutualDestruction()
        val homePlayer = getPlayerEpic(getBlueTeam())
        val awayPlayer = getPlayerSteam(getOrangeTeam())

        // homePlayer demo's first
        cut.interpret(demo(homePlayer, awayPlayer), Instant.parse("2020-08-30T18:43:00.000Z")) shouldHaveSize  0
        cut.interpret(demo(awayPlayer, homePlayer), Instant.parse("2020-08-30T18:43:00.090Z")) shouldContain Announcement.MUTUAL_DESTRUCTION

        // awayPlayer demo's first
        cut.interpret(demo(awayPlayer, homePlayer), Instant.parse("2020-08-30T18:46:00.000Z")) shouldHaveSize  0
        cut.interpret(demo(homePlayer, awayPlayer), Instant.parse("2020-08-30T18:46:00.090Z")) shouldContain Announcement.MUTUAL_DESTRUCTION

        // outside of time window
        cut.interpret(demo(homePlayer, awayPlayer), Instant.parse("2020-08-30T18:50:00.000Z")) shouldHaveSize  0
        cut.interpret(demo(awayPlayer, homePlayer), Instant.parse("2020-08-30T18:50:00.590Z")) shouldHaveSize  0
    }

    fun demo(player: JsonPlayer, victim: JsonPlayer) = parseStatMessage(
        JsonStatMessage(
            matchGUID = "123",
            event = "Demolish",
            player = player,
            victim = victim
        ))!!

}