package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class WitnessSave(val statRepository: StatRepository): StatToAnnouncment {
    override fun listenTo() = setOf(Events.DEMOLISH)

    private val witnessWindow = 2.seconds

    override fun interpret(
        statMessage: StatMessage,
        currentTimeStamp: Instant
    ): Set<Announcement> {
        if (statMessage.player.team!!.homeTeam!!) return emptySet()

        return if (statRepository.getStatHistory(statMessage.matchGUID)
                .filter { (instant, _) -> currentTimeStamp.minus(instant) < witnessWindow }
                .filter { (_, message) ->
                    Events.SAVE.eq(message.event) ||
                    Events.EPIC_SAVE.eq(message.event)
                }
                .count { (_, message) -> message.player.botSaveId() == statMessage.victim!!.botSaveId() } > 0) {
             setOf(Announcement.WITNESS)
        } else  emptySet()
    }
}