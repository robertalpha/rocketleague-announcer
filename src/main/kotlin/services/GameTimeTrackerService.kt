package nl.vanalphenict.services

import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage

class GameTimeTrackerService : EventHandler {

     private var latestGameTime: GameTimeMessage = GameTimeMessage("123", 300, false)

    override fun handleStatMessage(msg: StatMessage, metaData: RLAMetaData) {
        synchronized(this) {
            metaData.gameTime = latestGameTime
        }
    }

    override fun handleGameTime(msg: GameTimeMessage) {
        synchronized(this) {
            latestGameTime = msg
        }
    }

    fun getLatestGameTime(): GameTimeMessage {
        synchronized(this){
            return latestGameTime
        }
    }
}