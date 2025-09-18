package nl.vanalphenict.services


import com.janoz.discord.Voice
import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage

class AnnouncementHandler(private val voice: Voice, private val interpreters : List<StatToAnnouncment>) : EventHandler {


    val gid = System.getenv("GID")
    val vid = System.getenv("VID")

    val url: String =
        "${System.getenv("SBB_ADDRESS")}/api/guilds/${gid}/voicechannels/${vid}/play/718360%7C"

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
