package nl.vanalphenict.services

import com.janoz.discord.Voice
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage

class AnnouncementHandler(
    private val voice: Voice,
    private val interpreters : List<StatToAnnouncment>,
    private val sampleMapper: SampleMapper) : EventHandler {

    val gid: Long = System.getenv("GID").toLong()
    val vid: Long = System.getenv("VID").toLong()

    override fun handleStatMessage(msg: StatMessage) {
        val announcements = kotlin.collections.HashSet<Announcement>()
        interpreters.forEach {
            announcements.addAll(it.interpret(msg))
        }
        val sample = sampleMapper.getSample(announcements)
        if (sample != null) triggerSound(sample)
    }

    fun triggerSound(sample: String) {
        voice.play(sample, gid, vid)
    }
}
