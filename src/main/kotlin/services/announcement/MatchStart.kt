package nl.vanalphenict.services.announcement

import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameEvents
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.services.GameEventToAnnouncement

class MatchStart(private val gameEventRepository: GameEventRepository) : GameEventToAnnouncement {
    override fun listenTo() = setOf(GameEvents.ROUND_STARTED, GameEvents.COUNTDOWN_BEGIN)

    override fun interpret(
        statMessage: GameEventMessage,
        currentTimeStamp: Instant,
    ): Set<Announcement> {
        if (statMessage.gameEvent == GameEvents.COUNTDOWN_BEGIN) {
            return setOf(Announcement.COUNTDOWN_START)
        }

        return if (
            gameEventRepository.getGameEventHistory(statMessage.matchGuid).count { (_, event) ->
                GameEvents.ROUND_STARTED == event.gameEvent
            } == 0
        )
            setOf(Announcement.MATCH_START, Announcement.ROUND_START)
        else setOf(Announcement.ROUND_START)
    }
}
