package services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.JsonPlayer
import nl.vanalphenict.model.JsonStatMessage
import nl.vanalphenict.model.parseStatMessage
import nl.vanalphenict.services.announcement.Retaliation
import nl.vanalphenict.support.getBlueTeam
import nl.vanalphenict.support.getOrangeTeam
import nl.vanalphenict.support.getPlayerEpic
import nl.vanalphenict.support.getPlayerSteam
import nl.vanalphenict.support.getPlayerSwitch
import kotlin.test.Test
import kotlin.time.Instant


class RetaliationTest {


    @Test
    fun testRetaliation() {
        val cut = Retaliation()
        val homePlayer = getPlayerEpic(getBlueTeam())
        val otherHomePlayer = getPlayerSwitch(getBlueTeam())
        val awayPlayer = getPlayerSteam(getOrangeTeam())


        cut.interpret(demo(awayPlayer, homePlayer), Instant.parse("2020-08-30T18:43:02Z")) shouldHaveSize 0
        cut.interpret(demo(awayPlayer, homePlayer), Instant.parse("2020-08-30T18:43:03Z")) shouldHaveSize 0
        cut.interpret(demo(otherHomePlayer, awayPlayer), Instant.parse("2020-08-30T18:43:04Z")) shouldContain Announcement.RETALIATION
        cut.interpret(demo(homePlayer, awayPlayer), Instant.parse("2020-08-30T18:43:05Z")) shouldHaveSize 0
    }

    fun demo(player: JsonPlayer, victim: JsonPlayer) = parseStatMessage(
        JsonStatMessage(
            matchGUID = "123",
            event = "Demolish",
            player = player,
            victim = victim
        ))!!

}