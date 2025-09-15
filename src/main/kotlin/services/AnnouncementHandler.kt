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


    var hostname = "127.0.0.1"
    var endpoint = "/playsound"

    override fun handleStatMessage(msg: StatMessage) {
        var announcement : Announcement = Announcement.NOTHING

        interpreters.forEach {
            announcement = announcement.combine(it.interpret(msg))
        }

    }

    fun triggerSound(soundName: String) {


            val response: HttpResponse = runBlocking {
                client.post("$hostname$endpoint") {
                    setBody("$PREFIX${soundName}")
                }
            }
            if (response.status != HttpStatusCode.OK) {
                println("could not play ${soundName}, response: ${response.status}")
            }
        }


}