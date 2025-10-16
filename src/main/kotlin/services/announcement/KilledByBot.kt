package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.KillMessage
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant

class KilledByBot : StatToAnnouncment {
    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
        if (statMessage !is KillMessage) return emptySet()
        return if (
            statMessage.victim.team.homeTeam && statMessage.player.bot)
            setOf(Announcement.KILLED_BY_BOT)
        else emptySet()
    }
}