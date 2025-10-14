package nl.vanalphenict.services.impl

import kotlinx.coroutines.runBlocking
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.web.SSE_EVENT_TYPE
import nl.vanalphenict.web.triggerSSE

class SsePublisher : EventHandler {

    override fun handleStatMessage(
        msg: StatMessage,
        metaData: RLAMetaData
    ) {
        runBlocking {
            triggerSSE(SSE_EVENT_TYPE.NEW_ACTION)
        }
    }
}