package nl.vanalphenict.services

import com.janoz.discord.Voice
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage

class AnnouncementHandler(
    private val voice: Voice,
    private val interpreters : List<StatToAnnouncment>,
    private val sampleMapper: SampleMapper) : EventHandler {

    val groupIDEnv = System.getenv("GUILD_ID")
    
    val gid: Long = groupIDEnv.toLong()
    val vid: Long = System.getenv("VOICE_CHANNEL_ID").toLong()

    override fun handleStatMessage(msg: StatMessage) {
        val announcements = kotlin.collections.HashSet<Announcement>()
        interpreters.filter { interpreter ->
            interpreter.listenTo().any{event -> event.eq(msg.event)}
        }.forEach {
            announcements.addAll(it.interpret(msg))
        }
        val sample = sampleMapper.getSample(announcements)
        if (sample != null) triggerSound(sample)
    }

    fun triggerSound(sample: String) {
        voice.play(sample, gid, vid)
    }
}
