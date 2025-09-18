package nl.vanalphenict.services


import com.janoz.discord.Voice
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage

class AnnouncementHandler(private val voice: Voice?, private val interpreters : List<StatToAnnouncment>) : EventHandler {


    val gid = System.getenv("GID")?.toLong()
    val vid = System.getenv("VID")?.toLong()
    val soundpack = System.getenv("SOUNDPACK")?.toLong()

    override fun handleStatMessage(msg: StatMessage) {
        var announcement: Announcement = Announcement.NOTHING
        interpreters.forEach {
            announcement = announcement.combine(it.interpret(msg))
        }
        if (announcement == Announcement.NOTHING) return
        triggerSound(announcement)
    }

    fun triggerSound(announcement: Announcement) {
        voice?.play("$soundpack|${announcement}", gid?:0, vid?:0)
    }
}
