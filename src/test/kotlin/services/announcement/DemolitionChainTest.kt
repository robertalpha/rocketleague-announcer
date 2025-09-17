package services.announcement

import io.kotest.matchers.shouldBe
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
import kotlin.test.Test

class DemolitionChainTest {
    @Test
    fun interpret() {
        val repo = StatRepository()
        val cut = DemolitionChain(repo)


        repo.addStatMessage(Instant.parse("2020-08-30T18:43:00Z"), demoStatmessage())
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:02Z")) shouldBe Announcement.DOUBLE_KILL
        repo.addStatMessage(Instant.parse("2020-08-30T18:43:06Z"), demoStatmessage())
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:08Z")) shouldBe Announcement.TRIPLE_KILL
        repo.addStatMessage(Instant.parse("2020-08-30T18:43:08Z"), demoStatmessage())
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:09Z")) shouldBe Announcement.QUAD_KILL
        repo.addStatMessage(Instant.parse("2020-08-30T18:43:18Z"), demoStatmessage())
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:21Z")) shouldBe Announcement.PENTA_KILL

        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:32Z")) shouldBe Announcement.NOTHING
    }

    @Test
    fun interpret2() {
        val cut = DemoChain()
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:02Z")) shouldBe Announcement.NOTHING
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:03Z")) shouldBe Announcement.DOUBLE_KILL
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:04Z")) shouldBe Announcement.TRIPLE_KILL
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:05Z")) shouldBe Announcement.QUAD_KILL
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:06Z")) shouldBe Announcement.PENTA_KILL
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:07Z")) shouldBe Announcement.MASSACRE
        cut.interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:08Z")) shouldBe Announcement.MASSACRE

    }

    fun demoStatmessage() = StatMessage(
        matchGUID= "123",
        event= "Demolish",
        player= getPlayerEpic(getBlueTeam()),
        victim= getPlayerSteam(getOrangeTeam())
    )

}