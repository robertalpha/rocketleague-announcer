package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class KickOffKill(private val gameEventRepository: GameEventRepository): StatToAnnouncment{
    override fun listenTo() = setOf(Events.DEMOLISH)

    private val kickoffDuration = 1.seconds

    override fun interpret(
        statMessage: StatMessage,
        currentTimeStamp: Instant
    ): Set<Announcement> {

        return if (gameEventRepository.getGameEventHistory(statMessage.matchGUID)

            .filter { (instant, _) -> instant.plus(kickoffDuration) > currentTimeStamp }
            .count { (_,event) -> event.gameEvent.endsWith("StartRound") } > 0 )
            setOf(Announcement.KICK_OFF_KILL)
        else emptySet()
    }
}