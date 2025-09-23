package services.announcement
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment
import nl.vanalphenict.utility.TimeUtils.Companion.bothHappenWithin

class MutualDestruction(private val statRepository: StatRepository): StatToAnnouncment  {

    override fun listenTo() = setOf(Events.DEMOLISH)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {

            val lastFound = statRepository.getStatHistory(statMessage.matchGUID).lastOrNull{ (_,stat) -> setOfNotNull(stat.player.id, stat.victim?.id ).contains(statMessage.player.id)  }

        lastFound?.let {
            val (lastTimeStamp, lastPlayerDemo) = lastFound
            if (
                lastPlayerDemo.player.id == statMessage.victim?.id &&
                lastPlayerDemo.victim?.id == statMessage.player.id &&
                lastTimeStamp.bothHappenWithin(currentTimeStamp, 100.milliseconds)
            ) {
                return setOf(Announcement.MUTUAL_DESTRUCTION)
            }
        }

        return setOf()
    }
}