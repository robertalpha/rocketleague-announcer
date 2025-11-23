package nl.vanalphenict.services

import com.janoz.discord.DiscordService
import com.janoz.discord.domain.VoiceChannel

class SamplePlayer(val discordService: DiscordService, var voiceChannel: VoiceChannel? = null) {

    val randomPlaysCounter = mutableMapOf<String, Int>()
    val playCounter = mutableMapOf<String, Int>()

    fun play(sampleId: String?) =
        sampleId?.let {
            voiceChannel
                ?.let { discordService.play(sampleId, voiceChannel) }
                .also { playCounter[sampleId] = playCounter.getOrPut(sampleId) { 0 }.inc() }
        }

    fun playSemiRandom(ids: List<String>) {
        val counts = ids.map { Pair(it, randomPlaysCounter[it] ?: 0) }
        val minimumCount = counts.minOf { (_, count) -> count }

        val (selected, count) = counts.filter { it.second == minimumCount }.random()
        randomPlaysCounter[selected] = count + 1
        voiceChannel?.let { discordService.play(selected, voiceChannel) }
    }
}
