package services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.services.announcement.Retaliation
import nl.vanalphenict.support.blueTeam
import nl.vanalphenict.support.demoMessage
import nl.vanalphenict.support.orangeTeam
import nl.vanalphenict.support.playerEpic
import nl.vanalphenict.support.playerSteam

/**
 * Tests for [Retaliation] which detects when a contributor demolishes an away player who previously
 * demolished someone.
 */
class RetaliationTest {
    private lateinit var cut: Retaliation
    private val homePlayer = playerEpic(blueTeam())
    private val awayPlayer = playerSteam(orangeTeam())

    @BeforeTest
    fun setup() {
        cut = Retaliation()
    }

    @Test
    fun `should detect retaliation when contributor kills away player who recently demolished someone`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val t2 = t1.plus(5.seconds)

        // Away player kills Home player
        cut.interpret(demoMessage(awayPlayer, homePlayer), t1) shouldHaveSize 0

        // Home player kills that Away player
        cut.interpret(demoMessage(homePlayer, awayPlayer), t2) shouldContain
            Announcement.RETALIATION
    }

    @Test
    fun `should not detect retaliation if more than 10 seconds have passed`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val t2 = t1.plus(11.seconds)

        // Away player kills Home player
        cut.interpret(demoMessage(awayPlayer, homePlayer), t1) shouldHaveSize 0

        // Home player kills Away player after 11 seconds
        cut.interpret(demoMessage(homePlayer, awayPlayer), t2) shouldHaveSize 0
    }

    @Test
    fun `should not detect retaliation if the killer is not a contributor`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val t2 = t1.plus(5.seconds)
        val nonContributorHomeTeam = blueTeam().apply { hasContributors = false }
        val nonContributorHomePlayer = playerEpic(nonContributorHomeTeam)

        // Away player kills Home player
        cut.interpret(demoMessage(awayPlayer, homePlayer), t1) shouldHaveSize 0

        // Non-contributing player kills Away player
        cut.interpret(demoMessage(nonContributorHomePlayer, awayPlayer), t2) shouldHaveSize 0
    }

    @Test
    fun `should remove grudge after successful retaliation`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val t2 = t1.plus(5.seconds)
        val t3 = t1.plus(6.seconds)

        // Away player kills Home player
        cut.interpret(demoMessage(awayPlayer, homePlayer), t1) shouldHaveSize 0

        // Home player retaliates
        cut.interpret(demoMessage(homePlayer, awayPlayer), t2) shouldContain
            Announcement.RETALIATION

        // Second kill on the same away player should not trigger retaliation again
        cut.interpret(demoMessage(homePlayer, awayPlayer), t3) shouldHaveSize 0
    }

    @Test
    fun `should track multiple away player grudges independently`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val otherAwayPlayer = playerSteam(orangeTeam()).copy(id = "other-away")

        // Both away players kill someone
        cut.interpret(demoMessage(awayPlayer, homePlayer), t1)
        cut.interpret(demoMessage(otherAwayPlayer, homePlayer), t1.plus(1.seconds))

        // Retaliate against both
        cut.interpret(demoMessage(homePlayer, awayPlayer), t1.plus(5.seconds)) shouldContain
            Announcement.RETALIATION
        cut.interpret(demoMessage(homePlayer, otherAwayPlayer), t1.plus(6.seconds)) shouldContain
            Announcement.RETALIATION
    }
}
