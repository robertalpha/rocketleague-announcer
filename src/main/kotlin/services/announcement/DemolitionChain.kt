package nl.vanalphenict.services.announcement

import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment

class DemolitionChain(private val statRepository: StatRepository) : StatToAnnouncment {

    private val DEMOLISH = "Demolish"

    private val PIVOT_DURATION = 11.seconds

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Announcement {

        if (statMessage.event != DEMOLISH) return Announcement.NOTHING

        var demos = statRepository.getStatHistory(statMessage.matchGUID)
            .filter { it.second.event == DEMOLISH }
            .sortedByDescending { it.first }

        var pivot = currentTimeStamp
        var democounter = 1
        do {
            val head = demos.first()
            if(pivot.minus(head.first) < PIVOT_DURATION) {
                democounter++
                pivot = head.first
                demos = demos.drop(1)
            } else {
                demos = emptyList()
            }
        } while (demos.isNotEmpty())

        return when(democounter) {
            2 -> Announcement.DOUBLE_KILL
            3 -> Announcement.TRIPLE_KILL
            4 -> Announcement.QUAD_KILL
            5 -> Announcement.PENTA_KILL
            else -> Announcement.NOTHING
        }
    }
}