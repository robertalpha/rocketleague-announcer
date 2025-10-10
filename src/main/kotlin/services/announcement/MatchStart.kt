package nl.vanalphenict.services.announcement

import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.services.GameEventToAnnouncement

const val START_ROUND = "Function GameEvent_Soccar_TA.Active.StartRound"

class MatchStart(private val gameEventRepository: GameEventRepository): GameEventToAnnouncement {
    override fun listenTo() = setOf(START_ROUND)

    override fun interpret(
        statMessage: GameEventMessage,
        currentTimeStamp: Instant
    ): Set<Announcement> {

        return if (gameEventRepository.getGameEventHistory(statMessage.matchGUID).count { (_,event) -> event.gameEvent == START_ROUND } == 0 )
            setOf(Announcement.MATCH_START)
        else emptySet()
    }
}