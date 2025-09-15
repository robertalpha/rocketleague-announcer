package nl.vanalphenict.services

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage

class AnnouncementHandler(private val interpreters : List<StatToAnnouncment>) : EventHandler {

    override fun handleStatMessage(msg: StatMessage) {
        var announcement : Announcement = Announcement.NOTHING

        interpreters.forEach {
            announcement = announcement.combine(it.interpret(msg))
        }

    }
}