package nl.vanalphenict.services.announcement

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.KillMessage
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment
import nl.vanalphenict.utility.TimeUtils.Companion.bothHappenWithin

class Revenge() : StatToAnnouncment {
    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    private val grudges: MutableMap<Pair<String, String>, Instant> = HashMap()

    private val grudgeDuration = 60.seconds

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
        if (statMessage !is KillMessage) return emptySet()

        val current: Pair<String, String> = (statMessage.player.id) to (statMessage.victim.id)
        grudges[current] = currentTimeStamp
        val reverted: Pair<String, String> = current.second to current.first
        return if (
            grudges[reverted]?.let { grudge -> grudge.plus(grudgeDuration) > currentTimeStamp }
                ?: false
        ) {
            val isRevenge =
                !grudges[reverted]!!.bothHappenWithin(currentTimeStamp, 100.milliseconds)
            grudges.remove(reverted)
            if (isRevenge) {
                setOf(Announcement.REVENGE)
            } else emptySet()
        } else emptySet()
    }
}
