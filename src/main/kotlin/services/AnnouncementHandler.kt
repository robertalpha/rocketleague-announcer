package nl.vanalphenict.services

import com.janoz.discord.DiscordService
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage

class AnnouncementHandler(
    private val discordService: DiscordService,
    interpreters : Collection<StatToAnnouncment>,
    private var sampleMapper: SampleMapper) : EventHandler {

    private val interpreterMap : MutableMap<String, Set<StatToAnnouncment>> = HashMap()
    private val gid: Long = System.getenv("GUILD_ID")?.toLong()?:-1L
    private val vid: Long = System.getenv("VOICE_CHANNEL_ID")?.toLong()?:-2L

    init {
        interpreters.forEach { interpreter ->
            interpreter.listenTo().forEach { event ->
                    interpreterMap[event.eventName] =
                        (interpreterMap[event.eventName] ?: HashSet()).plus(interpreter)
            }
        }
    }

    override fun handleStatMessage(msg: StatMessage) {
        val announcements = HashSet<Announcement>()
        interpreterMap[msg.event]?.let {
            it.forEach { interpreter -> announcements.addAll(interpreter.interpret(msg)) }
        }
        sampleMapper.getSample(announcements)?.let { triggerSound(it) }
    }

    private fun triggerSound(sample: String) {
        discordService.play(sample, gid, vid)
    }

    fun replaceMapping(newMap : SampleMapper) {
        sampleMapper = newMap
    }
}
