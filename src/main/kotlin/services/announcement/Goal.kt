package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant

class Goal : StatToAnnouncment  {
    override fun listenTo(): Set<StatEvents> = setOf(StatEvents.GOAL)

    override fun interpret(
        statMessage: StatMessage,
        currentTimeStamp: Instant
    ) = setOfNotNull(
        Announcement.GOAL,
        if (statMessage.player.team.homeTeam)
            Announcement.GOAL_HOME
        else
            Announcement.GOAL_AWAY,
        if (statMessage.player.bot)
            Announcement.GOAL_BY_BOT
        else
            null
    )
}