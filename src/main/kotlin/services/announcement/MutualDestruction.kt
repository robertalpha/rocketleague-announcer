package nl.vanalphenict.services.announcement

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment
import nl.vanalphenict.utility.TimeUtils.Companion.bothHappenWithin

class MutualDestruction(): StatToAnnouncment  {

    val recentDemos = mutableListOf<Pair<Instant, StatMessage>>()
    val timeWindow = 500.milliseconds

    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
        // clear out older demolitions
        recentDemos.removeIf { (ts,_) ->
            !ts.bothHappenWithin(currentTimeStamp, timeWindow)
        }

        if(recentDemos.any { (_, previousDemo) ->
            previousDemo.player.id == statMessage.victim?.id &&
            previousDemo.victim?.id == statMessage.player.id
        }) {
            return setOf(Announcement.MUTUAL_DESTRUCTION)
        }

        recentDemos.add(Pair(currentTimeStamp, statMessage))

        return setOf()
    }
}