package nl.vanalphenict.services

import com.janoz.discord.DiscordService
import com.janoz.discord.domain.VoiceChannel
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage

class AnnouncementHandler(
    private val discordService: DiscordService,
    private val voiceChannel: VoiceChannel,
    private var sampleMapper: SampleMapper,
    interpreters: Collection<StatToAnnouncment>
) : EventHandler {

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
        sampleMapper.getPrevailingAnnouncement(announcements)?.let {
            sampleMapper.getSample(it) }?.let { triggerSound(it)
        }
    }

    private fun triggerSound(sample: String) {
        discordService.play(sample, voiceChannel)
    }

    fun replaceMapping(newMap : SampleMapper) {
        sampleMapper = newMap
    }
}
