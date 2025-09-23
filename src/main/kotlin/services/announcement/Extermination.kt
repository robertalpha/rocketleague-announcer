package services.announcement
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment

class Extermination(private val statRepository: StatRepository): StatToAnnouncment  {

    override fun listenTo() = setOf(Events.DEMOLISH)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {

            val playerKills = statRepository.getStatHistory(statMessage.matchGUID).filter { (_, stat) -> stat.player.id == statMessage.player.id && Events.DEMOLISH.eq(
                stat.event) }

        return when(playerKills.count()) {
            7 -> setOf(Announcement.EXTERMINATION)
            14 -> setOf(Announcement.EXTERMINATION_DOUBLE)
            else -> setOf()
        }
    }
}