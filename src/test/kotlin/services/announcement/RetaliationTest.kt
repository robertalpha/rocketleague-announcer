package services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Player
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.announcement.Retaliation
import support.getBlueTeam
import support.getOrangeTeam
import support.getPlayerEpic
import support.getPlayerSteam
import kotlin.test.Test
import kotlin.time.Instant


class RetaliationTest {


    @Test
    fun testRetaliation() {
        val cut = Retaliation()
        val p1 = getPlayerEpic(getBlueTeam())
        val p2 = getPlayerSteam(getOrangeTeam())


        cut.interpret(demo(p1,p2), Instant.parse("2020-08-30T18:43:02Z")) shouldHaveSize 0
        cut.interpret(demo(p1,p2), Instant.parse("2020-08-30T18:43:03Z")) shouldHaveSize 0
        cut.interpret(demo(p2,p1), Instant.parse("2020-08-30T18:43:04Z")) shouldContain Announcement.RETALIATION
        cut.interpret(demo(p2,p1), Instant.parse("2020-08-30T18:43:05Z")) shouldHaveSize 0


    }

    fun demo(player: Player, victim: Player) = StatMessage(
        matchGUID= "123",
        event= "Demolish",
        player= player,
        victim= victim)

}