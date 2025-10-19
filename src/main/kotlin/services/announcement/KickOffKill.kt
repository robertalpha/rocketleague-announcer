package nl.vanalphenict.services.announcement

import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.GameEvents
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.services.StatToAnnouncment
import nl.vanalphenict.utility.TimeUtils.Companion.bothHappenWithin

class KickOffKill(private val gameEventRepository: GameEventRepository) : StatToAnnouncment {
    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    private val kickoffDuration = 4.seconds

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {

        return if (
            gameEventRepository
                .getGameEventHistory(statMessage.matchGUID)
                .filter { (instant, _) ->
                    instant.bothHappenWithin(currentTimeStamp, kickoffDuration)
                }
                .count { (_, event) -> event.gameEvent == GameEvents.START_ROUND } > 0
        )
            setOf(Announcement.KICK_OFF_KILL)
        else emptySet()
    }
}
