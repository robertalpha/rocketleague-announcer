package nl.vanalphenict.services.announcement

import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment

class DemoChain : StatToAnnouncment {
    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    private val pivotDuration = 11.seconds

    private val data: MutableMap<String, Pair<Int, Instant>> = HashMap()

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {

        val streak = data.computeIfAbsent(statMessage.matchGUID) { Pair(0, currentTimeStamp) }

        if (statMessage.player.team?.homeTeam == false) {
            data.remove(statMessage.matchGUID)
            return if (streak.first > 1) setOf(Announcement.COMBO_BREAKER) else emptySet()
        }

        return if (currentTimeStamp.minus(streak.second) < pivotDuration) {
            val streakLength = streak.first + 1
            data[statMessage.matchGUID] = Pair(streakLength, currentTimeStamp)
            if (streakLength > 5) return setOf(Announcement.MASSACRE)
            if (streakLength < 2) return emptySet()
            setOf(
                listOf(
                    Announcement.DOUBLE_KILL,
                    Announcement.TRIPLE_KILL,
                    Announcement.QUAD_KILL,
                    Announcement.PENTA_KILL,
                )[streakLength - 2]
            )
        } else {
            data.remove(statMessage.matchGUID)
            emptySet()
        }
    }
}
