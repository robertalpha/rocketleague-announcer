package nl.vanalphenict.web

import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.cacheControl
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

data class SseEvent(val data: String, val event: String? = null, val id: String? = null)

enum class SSE_EVENT_TYPE {
    NEW_ACTION,
    SWITCH_THEME,
    HOME_SCORE,
    HOME_NAME,
    AWAY_SCORE,
    AWAY_NAME,
    GAME_TIME,
    TEAM_COLORS;

    fun asString() = this.name.lowercase()
}

val sseFlow = MutableSharedFlow<SseEvent>()

suspend fun triggerUpdateSSE(event: SSE_EVENT_TYPE, html: String) {
    sseFlow.emit(SseEvent(event = event.asString(), data = html))
}

fun Application.configureSSE() {

    routing { get("/sse") { call.respondSse(sseFlow) } }
}

suspend fun ApplicationCall.respondSse(eventFlow: Flow<SseEvent>) {
    response.cacheControl(CacheControl.NoCache(null))
    respondBytesWriter(contentType = ContentType.Text.EventStream) {
        eventFlow.collect { event ->
            if (event.id != null) {
                writeStringUtf8("id: ${event.id}\n")
            }

            if (event.event != null) {
                writeStringUtf8("event: ${event.event}\n")
            }

            for (dataLine in event.data.lines()) {
                writeStringUtf8("data: $dataLine\n")
            }
            writeStringUtf8("\n")
            flush()
        }
    }
}
