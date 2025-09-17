package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class DemoChain: StatToAnnouncment  {

    private val pivotDuration = 11.seconds

    private val data:MutableMap<String, Pair<Int, Instant>> = HashMap()

    override fun interpret(statMessage: StatMessage,currentTimeStamp: Instant): Announcement {
        if (!Events.DEMOLISH.eq(statMessage.event)) return Announcement.NOTHING

        val streak = data.computeIfAbsent(statMessage.matchGUID) { Pair(0, currentTimeStamp) }

        if (statMessage.player.team?.homeTeam == false) {
            data.remove(statMessage.matchGUID)
            return if (streak.first > 1) Announcement.COMBO_BREAKER
            else Announcement.NOTHING
        }

        return if (currentTimeStamp.minus(streak.second) < pivotDuration) {
            val streakLength = streak.first + 1
            data[statMessage.matchGUID] = Pair(streakLength, currentTimeStamp)
            if (streakLength > 5) return Announcement.MASSACRE
            listOf(
                Announcement.NOTHING,
                Announcement.DOUBLE_KILL,
                Announcement.TRIPLE_KILL,
                Announcement.QUAD_KILL,
                Announcement.PENTA_KILL
            )[streakLength-1]
        } else {
            data.remove(statMessage.matchGUID)
            Announcement.NOTHING
        }
    }
}