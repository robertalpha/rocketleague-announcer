package nl.vanalphenict.services.announcement
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment

class Extermination(private val statRepository: StatRepository): StatToAnnouncment  {

    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {

            val playerKills = statRepository.getStatHistory(statMessage.matchGUID)
                .filter { (_, stat) -> stat.player.id == statMessage.player.id &&
                        StatEvents.DEMOLISH == stat.event }

        return when(playerKills.count()) {
            6 -> setOf(Announcement.EXTERMINATION)
            13 -> setOf(Announcement.EXTERMINATION_DOUBLE)
            else -> setOf()
        }
    }
}