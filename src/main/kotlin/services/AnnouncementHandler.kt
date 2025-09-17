package nl.vanalphenict.services

import com.janoz.discord.Voice
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage

class AnnouncementHandler(private val voice: Voice, private val interpreters : List<StatToAnnouncment>) : EventHandler {

    val gid: Long = System.getenv("GID").toLong()
    val vid: Long = System.getenv("VID").toLong()

    override fun handleStatMessage(msg: StatMessage) {
        var announcement: Announcement = Announcement.NOTHING
        interpreters.forEach {
            announcement = announcement.combine(it.interpret(msg))
        }
        if (announcement == Announcement.NOTHING) return
        triggerSound(announcement)
    }

    fun triggerSound(announcement: Announcement) {
        voice.play("718360|${announcement}", gid, vid)
    }
}
