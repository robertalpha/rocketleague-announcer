package services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.announcement.DemoChain
import nl.vanalphenict.services.announcement.DemolitionChain
import support.getBlueTeam
import support.getOrangeTeam
import support.getPlayerEpic
import support.getPlayerSteam

class DemolitionChainTest {
    @Test
    fun interpret() {
        val repo = StatRepository()
        val cut = DemolitionChain(repo)


        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:00Z")) shouldBe Announcement.NOTHING
        repo.addStatMessage(Instant.parse("2020-08-30T18:43:00Z"), demoStatmessage())
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:02Z")) shouldContain Announcement.DOUBLE_KILL
        repo.addStatMessage(Instant.parse("2020-08-30T18:43:06Z"), demoStatmessage())
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:08Z")) shouldContain Announcement.TRIPLE_KILL
        repo.addStatMessage(Instant.parse("2020-08-30T18:43:08Z"), demoStatmessage())
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:09Z")) shouldContain Announcement.QUAD_KILL
        repo.addStatMessage(Instant.parse("2020-08-30T18:43:18Z"), demoStatmessage())
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:21Z")) shouldContain Announcement.PENTA_KILL

        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:32Z")) shouldHaveSize 0
    }

    @Test
    fun interpret2() {
        val cut = DemoChain()
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:02Z")) shouldHaveSize 0
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:03Z")) shouldContain  Announcement.DOUBLE_KILL
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:04Z")) shouldContain Announcement.TRIPLE_KILL
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:05Z")) shouldContain Announcement.QUAD_KILL
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:06Z")) shouldContain Announcement.PENTA_KILL
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:07Z")) shouldContain Announcement.MASSACRE
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:08Z")) shouldContain Announcement.MASSACRE

    }

    fun demoStatmessage() = StatMessage(
        matchGUID= "123",
        event= "Demolish",
        player= getPlayerEpic(getBlueTeam()),
        victim= getPlayerSteam(getOrangeTeam())
    )

}