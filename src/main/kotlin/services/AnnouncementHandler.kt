package nl.vanalphenict.services

import com.janoz.discord.DiscordService
import com.janoz.discord.domain.VoiceChannel
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.utility.DeJitter
import kotlin.time.Duration.Companion.milliseconds

class AnnouncementHandler(
    private val discordService: DiscordService,
    private val voiceChannel: VoiceChannel,
    private var sampleMapper: SampleMapper,
    interpreters: Collection<StatToAnnouncment>
) : EventHandler {

    private val interpreterMap : MutableMap<String, Set<StatToAnnouncment>> = HashMap()

    private val dejitter : DeJitter<Announcement> = DeJitter(
        timeToCombine = 100.milliseconds,
        comparator = { a, b -> sampleMapper.selector(a) - sampleMapper.selector(b)},
        action = { announcement -> triggerSound(sampleMapper.getSample(announcement)!!) }
    )

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
        val candidate = sampleMapper.getPrevailingAnnouncement(announcements)
        dejitter.add(candidate)
    }

    private fun triggerSound(sample: String) {
        discordService.play(sample, voiceChannel)
    }

    fun replaceMapping(newMap : SampleMapper) {
        sampleMapper = newMap
    }
}
