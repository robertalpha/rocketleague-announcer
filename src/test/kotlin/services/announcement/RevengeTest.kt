package services.announcement

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.services.announcement.Revenge
import nl.vanalphenict.support.blueTeam
import nl.vanalphenict.support.demoMessage
import nl.vanalphenict.support.orangeTeam
import nl.vanalphenict.support.playerEpic
import nl.vanalphenict.support.playerSteam

/**
 * Tests for [Revenge] which detects when a player demolishes someone who previously demolished
 * them.
 */
class RevengeTest {
    private lateinit var cut: Revenge
    private val homePlayer = playerEpic(blueTeam())
    private val awayPlayer = playerSteam(orangeTeam())

    @BeforeTest
    fun setup() {
        cut = Revenge()
    }

    @Test
    fun `should detect revenge when victim kills original killer within 60 seconds`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val t2 = t1.plus(30.seconds)

        // Away player kills Home player
        cut.interpret(demoMessage(awayPlayer, homePlayer), t1) shouldHaveSize 0

        // Home player takes revenge on Away player
        cut.interpret(demoMessage(homePlayer, awayPlayer), t2) shouldContain Announcement.REVENGE
    }

    @Test
    fun `should not detect revenge when killing a different player who didn't kill you`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val t2 = t1.plus(5.seconds)
        val otherAwayPlayer = playerSteam(orangeTeam()).copy(id = "other-away")

        // Away player kills Home player
        cut.interpret(demoMessage(awayPlayer, homePlayer), t1) shouldHaveSize 0

        // Home player kills a DIFFERENT away player
        cut.interpret(demoMessage(homePlayer, otherAwayPlayer), t2) shouldHaveSize 0
    }

    @Test
    fun `should not detect revenge after grudge expires (60 seconds)`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val t2 = t1.plus(61.seconds)

        // Away player kills Home player
        cut.interpret(demoMessage(awayPlayer, homePlayer), t1) shouldHaveSize 0

        // Home player kills Away player after expiration
        cut.interpret(demoMessage(homePlayer, awayPlayer), t2) shouldHaveSize 0
    }

    @Test
    fun `should ignore mutual destruction (within 100ms)`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val t2 = t1.plus(50.milliseconds) // Within 100ms window

        // Mutual destruction: A kills B, and B kills A almost simultaneously
        cut.interpret(demoMessage(awayPlayer, homePlayer), t1) shouldHaveSize 0
        cut.interpret(demoMessage(homePlayer, awayPlayer), t2) shouldHaveSize 0
    }

    @Test
    fun `should remove grudge after successful revenge`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val t2 = t1.plus(10.seconds)
        val t3 = t1.plus(20.seconds)

        // Away player kills Home player
        cut.interpret(demoMessage(awayPlayer, homePlayer), t1) shouldHaveSize 0

        // Home player takes revenge
        cut.interpret(demoMessage(homePlayer, awayPlayer), t2) shouldContain Announcement.REVENGE

        // Home player kills Away player again (no new revenge should trigger)
        cut.interpret(demoMessage(homePlayer, awayPlayer), t3) shouldHaveSize 0
    }

    @Test
    fun `should handle multiple independent grudges`() {
        val t1 = Instant.parse("2020-08-30T18:43:00Z")
        val otherHomePlayer = playerEpic(blueTeam()).copy(id = "other-home")

        // Away player kills both Home players
        cut.interpret(demoMessage(awayPlayer, homePlayer), t1)
        cut.interpret(demoMessage(awayPlayer, otherHomePlayer), t1.plus(1.seconds))

        // Both Home players can take revenge
        cut.interpret(demoMessage(homePlayer, awayPlayer), t1.plus(5.seconds)) shouldContain
            Announcement.REVENGE
        cut.interpret(demoMessage(otherHomePlayer, awayPlayer), t1.plus(6.seconds)) shouldContain
            Announcement.REVENGE
    }
}
