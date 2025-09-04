package nl.vanalphenict

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import nl.vanalphenict.messaging.MessagingClient
import nl.vanalphenict.util.TestMessage

fun Application.configureRouting(client: MessagingClient) {
    routing {
        get("/") {
            call.respondText("Hello world!")
        }
        post("/") {
            val msg = call.receive<TestMessage>()
            client.send(msg.topic, msg.message.toString())
            call.respond(HttpStatusCode.OK)
            println(msg)
        }
    }
}
