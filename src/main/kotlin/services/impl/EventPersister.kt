package nl.vanalphenict.services.impl

import kotlinx.coroutines.runBlocking
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.li
import kotlinx.html.stream.createHTML
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.utility.TimeService
import nl.vanalphenict.web.SSE_EVENT_TYPE
import nl.vanalphenict.web.triggerUpdateSSE
import nl.vanalphenict.web.view.actionListItem

class EventPersister(
    val statRepository: StatRepository,
    val gameEventRepository: GameEventRepository,
    val timeService: TimeService) : EventHandler {

    override fun handleStatMessage(
        msg: StatMessage,
        metaData: RLAMetaData
    ) {
        synchronized(this) {
            statRepository.addStatMessage(timeService.now(), msg)
        }
    }

    override fun handleGameEvent(
        msg: GameEventMessage,
        metaData: RLAMetaData
    ) {
        synchronized(this) {
            gameEventRepository.addGameEventMessage(timeService.now(), msg)
        }
    }
}