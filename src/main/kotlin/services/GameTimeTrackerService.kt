package nl.vanalphenict.services

import nl.vanalphenict.model.GameTimeMessage

class GameTimeTrackerService : EventHandler {

     private var latestGameTime: GameTimeMessage = GameTimeMessage("123", 300, false)


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