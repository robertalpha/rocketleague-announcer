package nl.vanalphenict.services.impl

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import kotlinx.html.body
import kotlinx.html.stream.createHTML
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.utility.TimeService
import nl.vanalphenict.web.SSE_EVENT_TYPE
import nl.vanalphenict.web.triggerUpdateSSE
import nl.vanalphenict.web.view.actionListItem

class SsePublisher(val timeService: TimeService) : EventHandler {

    private val log = KotlinLogging.logger {}

    override fun handleStatMessage(
        msg: StatMessage,
        metaData: RLAMetaData
    ) {
        log.trace {"SSE HANDLER" }
        val actionItem = Pair(timeService.now(), msg)
        val htmlText = createHTML().body {
            actionListItem(actionItem, metaData.remaining)
        }
        runBlocking {  triggerUpdateSSE(SSE_EVENT_TYPE.NEW_ACTION, htmlText) }
    }
}