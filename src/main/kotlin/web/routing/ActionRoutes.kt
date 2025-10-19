package nl.vanalphenict.web.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.body
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.GameTimeTrackerService
import nl.vanalphenict.web.view.actionListItem

fun Application.actionRoutes(
    statRepository: StatRepository,
    gameTimeTrackerService: GameTimeTrackerService,
) {
    routing {
        get("/actions") {
            val actions = statRepository.getLatestMatch()

            call.respondHtml {
                body {
                    for (actionItem in actions.sortedByDescending { (timestamp, _) -> timestamp }) {
                        val gameTime =
                            gameTimeTrackerService.getGameTime(actionItem.second.matchGUID)
                        actionListItem(actionItem, gameTime.remaining, gameTime.overtime)
                    }
                }
            }
        }

        // Read actionListItem
        get("/actions/{id}") {
            val id =
                call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Invalid ID")

            try {
                val action = statRepository.findByHash(id)!!
                call.respond(HttpStatusCode.OK, message = action)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
