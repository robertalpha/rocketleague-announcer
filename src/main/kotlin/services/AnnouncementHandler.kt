package nl.vanalphenict.services

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import java.text.NumberFormat.Field.PREFIX
import kotlinx.coroutines.runBlocking
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage

class AnnouncementHandler(private val interpreters : List<StatToAnnouncment>) : EventHandler {
    var client : HttpClient = HttpClient()


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

    fun triggerSound(soundName: String) {

    fun triggerSound(announcement: Announcement) {
        println("playing ${announcement.name}")

        val url = "$url${announcement.name}"
        val response: HttpResponse = runBlocking {
            client.put(url)
        }
        if (response.status != HttpStatusCode.OK) {
            println("could not play ${announcement}, response: ${response.status}")
        }
    }
}

}