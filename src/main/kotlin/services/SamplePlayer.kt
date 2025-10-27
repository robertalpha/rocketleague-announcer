package nl.vanalphenict.services

import com.janoz.discord.DiscordService
import com.janoz.discord.domain.VoiceChannel

class SamplePlayer (val discordService: DiscordService, var voiceChannel: VoiceChannel? = null)
{
    fun play(sampleId: String?) {
        if (sampleId == null || voiceChannel == null) return
        discordService.play(sampleId, voiceChannel)
    }
}