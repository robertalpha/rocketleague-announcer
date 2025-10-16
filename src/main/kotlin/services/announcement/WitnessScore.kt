package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.KillMessage
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
        if (!statMessage.player.team.homeTeam) return emptySet()

        return if (statRepository.getStatHistory(statMessage.matchGUID)
                .filter { (instant, _) -> currentTimeStamp.minus(instant) < witnessWindow }
                .count { (_, message) -> message is KillMessage &&
                        statMessage.player.id == message.victim.id } > 0) {
            setOf(Announcement.WITNESS)
        } else emptySet()
    }
}