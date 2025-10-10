package nl.vanalphenict.services

import com.janoz.discord.DiscordService
import com.janoz.discord.domain.VoiceChannel
import kotlin.time.Duration.Companion.milliseconds
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.utility.DeJitter

class AnnouncementHandler(
    private val discordService: DiscordService,
    private val voiceChannel: VoiceChannel,
    private var sampleMapper: SampleMapper,
    statInterpreters: Collection<StatToAnnouncment>,
    gameEventInterpreters: Collection<GameEventToAnnouncement>,
) : EventHandler {

    private val statInterpreterMap : MutableMap<String, Set<StatToAnnouncment>> = HashMap()
    private val gameEventInterpreterMap : MutableMap<String, MutableSet<GameEventToAnnouncement>> = HashMap()

    private val dejitter : DeJitter<Announcement> = DeJitter(
        timeToCombine = 100.milliseconds,
        comparator = { a, b -> sampleMapper.selector(a) - sampleMapper.selector(b)},
        action = { announcement -> triggerSound(sampleMapper.getSample(announcement)!!) }
    )

    init {
        statInterpreters.forEach { interpreter ->
            interpreter.listenTo().forEach { event ->
                    statInterpreterMap[event.eventName] =
                        (statInterpreterMap[event.eventName] ?: HashSet()).plus(interpreter)
            }
        }
        gameEventInterpreters.forEach { interpreter ->
            interpreter.listenTo().forEach { event ->
                    gameEventInterpreterMap.getOrPut(event.eventName) { HashSet() }.add(interpreter)
            }
        }
    }

    override fun handleStatMessage(msg: StatMessage) {
        val announcements = HashSet<Announcement>()
        statInterpreterMap[msg.event]?.let {
            it.forEach { interpreter -> announcements.addAll(interpreter.interpret(msg)) }
        }
        val candidate = sampleMapper.getPrevailingAnnouncement(announcements)
        dejitter.add(candidate)
    }

    override fun handleGameEvent(msg: GameEventMessage) {
        val announcements = HashSet<Announcement>()
        gameEventInterpreterMap[msg.gameEvent]?.let {
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
