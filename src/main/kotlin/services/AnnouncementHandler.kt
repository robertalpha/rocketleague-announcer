package nl.vanalphenict.services

import com.janoz.discord.DiscordService
import com.janoz.discord.domain.VoiceChannel
import kotlin.time.Duration.Companion.milliseconds
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.RLAMetaData
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

    override fun handleStatMessage(msg: StatMessage, metaData: RLAMetaData) {
        val announcements = HashSet<Announcement>()
        statInterpreterMap[msg.event]?.let {
            it.forEach { interpreter -> announcements.addAll(interpreter.interpret(msg)) }
        }
        metaData.announcements.addAll(announcements)
        val candidate = sampleMapper.getPrevailingAnnouncement(announcements)
        metaData.prevailingAnnouncement = candidate
        dejitter.add(candidate)
    }

    override fun handleGameTime(msg: GameTimeMessage) {
        if (!msg.overtime) {
            when (msg.remaining) {
                1 -> dejitter.add(Announcement.LEFT_1)
                2 -> dejitter.add(Announcement.LEFT_2)
                3 -> dejitter.add(Announcement.LEFT_3)
                4 -> dejitter.add(Announcement.LEFT_4)
                5 -> dejitter.add(Announcement.LEFT_5)
            }
        }
    }

    override fun handleGameEvent(msg: GameEventMessage, metaData: RLAMetaData) {
        val announcements = HashSet<Announcement>()
        gameEventInterpreterMap[msg.gameEvent]?.let {
            it.forEach { interpreter -> announcements.addAll(interpreter.interpret(msg)) }
        }
        metaData.announcements.addAll(announcements)
        val candidate = sampleMapper.getPrevailingAnnouncement(announcements)
        metaData.prevailingAnnouncement = candidate
        dejitter.add(candidate)
    }

    private fun triggerSound(sample: String) {
        discordService.play(sample, voiceChannel)
    }

    fun replaceMapping(newMap : SampleMapper) {
        sampleMapper = newMap
    }
}
