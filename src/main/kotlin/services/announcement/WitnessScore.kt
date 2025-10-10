package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class WitnessScore(val statRepository: StatRepository): StatToAnnouncment {
    override fun listenTo() = setOf(StatEvents.GOAL)

    private val witnessWindow = 3.seconds

    override fun interpret(statMessage: StatMessage,currentTimeStamp: Instant): Set<Announcement> {
        if (!statMessage.player.team!!.homeTeam!!) return emptySet()

        return if (statRepository.getStatHistory(statMessage.matchGUID)
                .filter { (instant, _) -> currentTimeStamp.minus(instant) < witnessWindow }
                .filter { (_, message) -> StatEvents.DEMOLISH.eq(message.event) }
                .count { (_, message) -> statMessage.player.isSame(message.victim) } > 0) {
            setOf(Announcement.WITNESS)
        } else emptySet()
    }
}