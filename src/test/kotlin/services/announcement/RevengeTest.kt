package services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Player
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.announcement.Revenge
import nl.vanalphenict.support.getBlueTeam
import nl.vanalphenict.support.getOrangeTeam
import nl.vanalphenict.support.getPlayerEpic
import nl.vanalphenict.support.getPlayerSteam
import nl.vanalphenict.support.getPlayerSwitch
import kotlin.test.Test
import kotlin.time.Instant


class RevengeTest {


    @Test
    fun testRetaliation() {
        val cut = Revenge()
        val homePlayer = getPlayerEpic(getBlueTeam())
        val otherHomePlayer = getPlayerSwitch(getBlueTeam())
        val awayPlayer = getPlayerSteam(getOrangeTeam())


        cut.interpret(demo(awayPlayer, homePlayer), Instant.parse("2020-08-30T18:43:02Z")) shouldHaveSize 0
        cut.interpret(demo(awayPlayer, homePlayer), Instant.parse("2020-08-30T18:43:03Z")) shouldHaveSize 0
        cut.interpret(demo(otherHomePlayer, awayPlayer), Instant.parse("2020-08-30T18:43:04Z")) shouldHaveSize 0
        cut.interpret(demo(homePlayer, awayPlayer), Instant.parse("2020-08-30T18:43:05Z")) shouldContain Announcement.REVENGE
        cut.interpret(demo(homePlayer, awayPlayer), Instant.parse("2020-08-30T18:43:06Z")) shouldHaveSize 0
    }

    fun demo(player: Player, victim: Player) = StatMessage(
        matchGUID= "123",
        event= "Demolish",
        player= player,
        victim= victim)

}