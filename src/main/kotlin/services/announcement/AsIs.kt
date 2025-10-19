package nl.vanalphenict.services.announcement

import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment

class AsIs : StatToAnnouncment {

    private val mapping: Map<StatEvents, Announcement> =
        mapOf(
            //        Events.DEMOLITION to Announcement.EXECUTIONER, //Handled by Extermination.kt
            StatEvents.AERIAL_GOAL to Announcement.AERIAL_GOAL,
            StatEvents.BACKWARDS_GOAL to Announcement.BACKWARDS_GOAL,
            StatEvents.BICYCLE_GOAL to Announcement.BICYCLE_GOAL,
            StatEvents.LONG_GOAL to Announcement.LONG_GOAL,
            StatEvents.TURTLE_GOAL to Announcement.TURTLE_GOAL,
            StatEvents.POOL_SHOT to Announcement.POOL_SHOT,
            StatEvents.HATTRICK to Announcement.HATTRICK,
            StatEvents.SAVE to Announcement.SAVE,
            StatEvents.EPIC_SAVE to Announcement.EPIC_SAVE,
            StatEvents.OWN_GOAL to Announcement.OWN_GOAL,
        )

    override fun listenTo(): Set<StatEvents> {
        return mapping.keys
    }

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
        return mapping
            .filter { (e, _) -> e == statMessage.event }
            .map { (_, a) -> setOf(a) }
            .firstOrNull() ?: emptySet()
    }
}
