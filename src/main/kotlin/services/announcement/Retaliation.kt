package nl.vanalphenict.services.announcement

import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment

class Retaliation() : StatToAnnouncment {
    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    private val grudges : MutableMap<String, Instant> = HashMap()

    private val grudgeDuration = 10.seconds

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {

        val killer = statMessage.player
        val victim = statMessage.victim!!

        if (killer.team!!.homeTeam!!) {
            val grudge: Instant? = grudges.remove(victim.botSaveId())
            if (grudge != null && grudge.plus(grudgeDuration) > currentTimeStamp) {
                return setOf(Announcement.RETALIATION)
            }
        } else {
            grudges[killer.botSaveId()] = currentTimeStamp
        }
        return emptySet()
    }

}
