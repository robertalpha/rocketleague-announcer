package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.KillMessage
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class WitnessSave(val statRepository: StatRepository): StatToAnnouncment {
    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    private val witnessWindow = 2.seconds

    override fun interpret(
        statMessage: StatMessage,
        currentTimeStamp: Instant
    ): Set<Announcement> {
        if (statMessage.player.team.homeTeam) return emptySet()
        if (statMessage !is KillMessage) return emptySet()

        return if (statRepository.getStatHistory(statMessage.matchGUID)
                .filter { (instant, _) -> currentTimeStamp.minus(instant) < witnessWindow }
                .filter { (_, message) ->
                    (StatEvents.SAVE == message.event) ||
                    (StatEvents.EPIC_SAVE == message.event)
                }
                .count { (_, message) -> message.player.id == statMessage.victim.id } > 0) {
             setOf(Announcement.WITNESS)
        } else  emptySet()
    }
}