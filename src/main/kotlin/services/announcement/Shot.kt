package nl.vanalphenict.services.announcement

import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment

class Shot : StatToAnnouncment {
    override fun listenTo(): Set<StatEvents> = setOf(StatEvents.SHOT)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant) =
        setOfNotNull(
            Announcement.SHOT,
            if (statMessage.player.team.homeTeam) Announcement.SHOT_HOME
            else Announcement.SHOT_AWAY,
            if (statMessage.player.bot) Announcement.SHOT_BY_BOT else null,
        )
}
