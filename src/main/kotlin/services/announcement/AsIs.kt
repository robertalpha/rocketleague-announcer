package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant

class AsIs: StatToAnnouncment   {

    private val mapping:Map <Events, Announcement> = mapOf(
//        Events.DEMOLITION to Announcement.EXECUTIONER, //Handled by Extermination.kt
        Events.AERIAL_GOAL to Announcement.AERIAL_GOAL,
        Events.BACKWARD_GOAL to Announcement.BACKWARD_GOAL,
        Events.BICYCLE_GOAL to Announcement.BICYCLE_GOAL,
        Events.LONG_GOAL to Announcement.LONG_GOAL,
        Events.TURTLE_GOAL to Announcement.TURTLE_GOAL,
        Events.POOL_SHOT to Announcement.POOL_SHOT,
        Events.HATTRICK to Announcement.HATTRICK,
        Events.SAVE to Announcement.SAVE,
        Events.EPIC_SAVE to Announcement.EPIC_SAVE,
        Events.OWN_GOAL to Announcement.OWN_GOAL
    )

    override fun listenTo(): Set<Events> {
        return mapping.keys
    }

    override fun interpret(statMessage: StatMessage,currentTimeStamp: Instant): Set<Announcement> {
        return mapping
            .filter { (e,_) -> e.eq(statMessage.event) }
            .map{ (_,a) -> setOf(a) }
            .getOrElse(0) { emptySet() }
    }
}