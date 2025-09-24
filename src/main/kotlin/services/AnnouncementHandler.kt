package nl.vanalphenict.services

import com.janoz.discord.DiscordService
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage

class AnnouncementHandler(
    private val discordService: DiscordService,
    private val interpreters : List<StatToAnnouncment>,
    private var sampleMapper: SampleMapper) : EventHandler {

    val gid: Long = System.getenv("GUILD_ID").toLong()
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

    private fun triggerSound(sample: String) {
        discordService.play(sample, gid, vid)
    }

    fun replaceMapping(newMap : SampleMapper) {
        sampleMapper = newMap
    }
}
