package nl.vanalphenict.web.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.BODY
import kotlinx.html.LI
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.visit
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.web.view.actionListItem

fun Application.actionRoutes(statRepository: StatRepository) {
    routing {
        get("/actions") {
            val actions = statRepository.getLatestMatch()

            call.respondHtml {
                body {
                    for (actionItem in actions.sortedByDescending { (timestamp,_ ) -> timestamp }) {
                        li {
                            classes=setOf("py-3","sm:py-4")
                            actionListItem(actionItem, actionItem.first.toEpochMilliseconds() + actionItem.second.hashCode())
                        }
                    }
                }
            }
        }

        // Read actionListItem
        get("/actions/{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Invalid ID")

            try {
                val action = statRepository.findByHash(id)!!
                call.respond(HttpStatusCode.OK, message = action)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

fun BODY.li(e: LI.() -> Unit) = LI(
    initialAttributes = mutableMapOf(),
    consumer = consumer
).visit(e)