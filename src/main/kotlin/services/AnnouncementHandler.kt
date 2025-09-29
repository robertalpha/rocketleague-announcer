package nl.vanalphenict.services

import com.janoz.discord.DiscordService
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage

class AnnouncementHandler(
    private val discordService: DiscordService,
    interpreters : Collection<StatToAnnouncment>,
    private val guildId: Long,
    private val voiceChannelId: Long,
    private var sampleMapper: SampleMapper) : EventHandler {

    private val interpreterMap : MutableMap<String, Set<StatToAnnouncment>> = HashMap()

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
        discordService.play(sample, guildId, voiceChannelId)
    }

    fun replaceMapping(newMap : SampleMapper) {
        sampleMapper = newMap
    }
}
