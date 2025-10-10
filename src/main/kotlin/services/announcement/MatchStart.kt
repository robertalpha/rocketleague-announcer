package nl.vanalphenict.services.announcement

import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameEvents
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.services.GameEventToAnnouncement

class MatchStart(private val gameEventRepository: GameEventRepository): GameEventToAnnouncement {
    override fun listenTo() = setOf(GameEvents.START_ROUND)

    override fun interpret(
        statMessage: GameEventMessage,
        currentTimeStamp: Instant
    ): Set<Announcement> {

        return if (gameEventRepository.getGameEventHistory(statMessage.matchGUID)
            .count { (_,event) -> GameEvents.START_ROUND.eq(event.gameEvent) } == 0 )
            setOf(Announcement.MATCH_START)
        else emptySet()
    }
}