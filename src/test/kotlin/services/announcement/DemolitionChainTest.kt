package services.announcement

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.announcement.DemolitionChain
import support.getBlueTeam
import support.getOrangeTeam
import support.getPlayerEpic
import support.getPlayerSteam

class DemolitionChainTest {
    @Test
    fun interpret() {
        val repo = StatRepository()

        DemolitionChain(repo).interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:00Z")) shouldBe Announcement.NOTHING

        repo.addStatMessage(Instant.parse("2020-08-30T18:43:00Z"), demoStatmessage())
        DemolitionChain(repo).interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:02Z")) shouldBe Announcement.DOUBLE_KILL
        repo.addStatMessage(Instant.parse("2020-08-30T18:43:06Z"), demoStatmessage())
        DemolitionChain(repo).interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:08Z")) shouldBe Announcement.TRIPLE_KILL
        repo.addStatMessage(Instant.parse("2020-08-30T18:43:08Z"), demoStatmessage())
        DemolitionChain(repo).interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:09Z")) shouldBe Announcement.QUAD_KILL
        repo.addStatMessage(Instant.parse("2020-08-30T18:43:18Z"), demoStatmessage())
        DemolitionChain(repo).interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:21Z")) shouldBe Announcement.PENTA_KILL

        DemolitionChain(repo).interpret(demoStatmessage(), Instant.parse("2020-08-30T18:43:32Z")) shouldBe Announcement.NOTHING
    }

    fun demoStatmessage() = StatMessage(
        matchGUID= "123",
        event= "Demolish",
        player= getPlayerEpic(getBlueTeam()),
        victim= getPlayerSteam(getOrangeTeam())
    )

}