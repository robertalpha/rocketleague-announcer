package services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.announcement.DemolitionChain
import nl.vanalphenict.support.blueTeam
import nl.vanalphenict.support.demoMessage
import nl.vanalphenict.support.orangeTeam
import nl.vanalphenict.support.playerEpic
import nl.vanalphenict.support.playerSteam

/**
 * Tests for [DemolitionChain] which uses a [StatRepository] to detect demolition streaks.
 */
class DemolitionChainTest {
    private lateinit var repo: StatRepository
    private lateinit var cut: DemolitionChain
    private val metaData = RLAMetaData(matchGuid = "123", overtime = false, remaining = 100.seconds)

    @BeforeTest
    fun setup() {
        repo = StatRepository()
        cut = DemolitionChain(repo)
    }

    @Test
    fun `should detect double kill when two demolitions occur within pivot duration`() {
        val firstDemoTime = Instant.parse("2020-08-30T18:43:00Z")
        val secondDemoTime = Instant.parse("2020-08-30T18:43:02Z")

        cut.interpret(demoStatMessage(), firstDemoTime) shouldHaveSize 0
        repo.addStatMessage(firstDemoTime, demoStatMessage(), metaData)

        cut.interpret(demoStatMessage(), secondDemoTime) shouldContain Announcement.DOUBLE_KILL
    }

    @Test
    fun `should detect triple kill when three demolitions occur within pivot duration`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val t2 = Instant.parse("2020-08-30T18:43:02Z")
        val t3 = Instant.parse("2020-08-30T18:43:04Z")

        repo.addStatMessage(t1, demoStatMessage(), metaData)
        repo.addStatMessage(t2, demoStatMessage(), metaData)

        cut.interpret(demoStatMessage(), t3) shouldContain Announcement.TRIPLE_KILL
    }

    @Test
    fun `should detect penta kill when five demolitions occur within pivot duration`() {
        val times = listOf(
            "2020-08-30T18:43:00Z",
            "2020-08-30T18:43:02Z",
            "2020-08-30T18:43:04Z",
            "2020-08-30T18:43:06Z",
            "2020-08-30T18:43:08Z"
        ).map { Instant.parse(it) }

        times.dropLast(1).forEach { repo.addStatMessage(it, demoStatMessage(), metaData) }

        cut.interpret(demoStatMessage(), times.last()) shouldContain Announcement.PENTA_KILL
    }

    @Test
    fun `should reset chain when demolition occurs after pivot duration`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val t2 = Instant.parse("2020-08-30T18:43:12Z") // > 11 seconds after t1

        repo.addStatMessage(t1, demoStatMessage(), metaData)

        cut.interpret(demoStatMessage(), t2) shouldHaveSize 0
    }

    @Test
    fun `should return empty set if team has no contributors`() {
        val nonContributorTeam = blueTeam().apply { hasContributors = false }
        val msg = demoMessage(playerEpic(team = nonContributorTeam), playerSteam())

        cut.interpret(msg, Instant.parse("2020-08-30T18:43:02Z")) shouldHaveSize 0
    }

    private fun demoStatMessage() = demoMessage(playerEpic(blueTeam()), playerSteam(orangeTeam()))
}
