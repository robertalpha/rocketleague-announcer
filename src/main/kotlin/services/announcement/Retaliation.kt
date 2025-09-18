package nl.vanalphenict.services.announcement

import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment

class Retaliation() : StatToAnnouncment {

    private val grudges : MutableMap<Pair<String,String>, Instant> = HashMap()

    private val grudgeDuration = 30.seconds

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
        if (!Events.DEMOLISH.eq(statMessage.event)) return emptySet()

        val current:Pair<String,String> =  (statMessage.player.botSaveId()) to (statMessage.victim!!.botSaveId())
        grudges[current] = currentTimeStamp
        val reverted:Pair<String,String> = current.second to current.first
        return if (grudges.containsKey(reverted) && grudges[reverted]!!.plus(grudgeDuration) > currentTimeStamp) {
            grudges.remove(reverted)
            setOf(Announcement.REVENGE)
        } else emptySet()
    }

}
