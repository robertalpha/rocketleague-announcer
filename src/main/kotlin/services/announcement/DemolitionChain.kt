package nl.vanalphenict.services.announcement

import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment

class DemolitionChain(private val statRepository: StatRepository) : StatToAnnouncment {
    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    private val PIVOT_DURATION = 11.seconds

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {

        val demoCount =
            getDemoCountForTeam(
                statMessage.matchGuid,
                { it == statMessage.player.team.teamNum },
                currentTimeStamp,
            )

        val demoCountOpponent =
            getDemoCountForTeam(
                statMessage.matchGuid,
                { it != statMessage.player.team.teamNum },
                currentTimeStamp,
            )

        return when (demoCount) {
            2 -> setOf(Announcement.DOUBLE_KILL)
            3 -> setOf(Announcement.TRIPLE_KILL)
            4 -> setOf(Announcement.QUAD_KILL)
            5 -> setOf(Announcement.PENTA_KILL)
            else -> emptySet()
        }
    }

    private fun getDemoCountForTeam(
        matchGuid: String,
        teamFilter: (Int) -> Boolean,
        currentTimeStamp: Instant,
    ): Int {
        var demos =
            statRepository
                .getStatHistory(matchGuid)
                .filter { (_, message) -> StatEvents.DEMOLISH == message.event }
                .filter { (_, message) -> teamFilter(message.player.team.teamNum) }
                .sortedByDescending { it.timestamp }

        var pivot = currentTimeStamp
        var democounter = 1
        do {
            val head = demos.first()
            if (pivot.minus(head.timestamp) < PIVOT_DURATION) {
                democounter++
                pivot = head.timestamp
                demos = demos.drop(1)
            } else {
                demos = emptyList()
            }
        } while (demos.isNotEmpty())
        return democounter
    }
}
