package nl.vanalphenict.support

import com.janoz.discord.domain.Guild
import com.janoz.discord.domain.VoiceChannel
import kotlin.time.Duration.Companion.seconds
import nl.vanalphenict.model.KillMessage
import nl.vanalphenict.model.Player
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.model.Team
import nl.vanalphenict.utility.ColorUtils

// ── Domain-level fixtures (for announcement / service tests) ────────────────

fun blueTeam(score: Int = 0) =
    Team(
        teamNum = 0,
        score = score,
        primaryColor = ColorUtils.BLUE,
        secondaryColor = ColorUtils.DARK_GREY,
        name = "TEAM BLUE",
        tag = "BLUE",
        hasContributors = true,
    )

fun orangeTeam(score: Int = 0) =
    Team(
        teamNum = 1,
        score = score,
        primaryColor = ColorUtils.ORANGE,
        secondaryColor = ColorUtils.DARK_GREY,
        name = "TEAM ORANGE",
        tag = "ORNG",
        hasContributors = false,
    )

fun playerEpic(team: Team = orangeTeam(), shortcut: Int = 0) =
    Player(
        id = "Epic|12345678cafebabe12345678cafebabe|0",
        name = "Jones",
        bot = false,
        shortcut = shortcut,
        teamNum = team.teamNum,
        team = team,
    )

fun playerSteam(team: Team = blueTeam(), shortcut: Int = 0) =
    Player(
        id = "Steam|12345678901234567|0",
        name = "Gordon",
        bot = false,
        shortcut = shortcut,
        teamNum = team.teamNum,
        team = team,
    )

fun playerSwitch(team: Team = blueTeam(), shortcut: Int = 0) =
    Player(
        id = "Switch|12345678901234567890|0",
        name = "Mario",
        bot = false,
        shortcut = shortcut,
        teamNum = team.teamNum,
        team = team,
    )

fun playerPlaystation(team: Team = orangeTeam(), shortcut: Int = 0) =
    Player(
        id = "PS4|1234567890123456789|0",
        name = "Snake",
        bot = false,
        shortcut = shortcut,
        teamNum = team.teamNum,
        team = team,
    )

fun botPlayer(team: Team = orangeTeam(), shortcut: Int = 0) =
    Player(
        id = "bot|Maverick|0",
        name = "Maverick",
        bot = true,
        shortcut = shortcut,
        teamNum = team.teamNum,
        team = team,
    )

fun demoMessage(attacker: Player, victim: Player, matchGuid: String = "123") =
    KillMessage(
        matchGuid = matchGuid,
        event = StatEvents.DEMOLISH,
        player = attacker,
        victim = victim,
    )

fun statMessage(event: StatEvents, player: Player = playerEpic(), matchGuid: String = "123abc") =
    StatMessage(matchGuid = matchGuid, event = event, player = player)

fun getMetaData() = RLAMetaData(matchGuid = "123abc", overtime = false, remaining = 300.seconds)

// ── Discord fixtures ────────────────────────────────────────────────────────

fun getVoiceChannel(guildId: Long = 1, voiceChannelId: Long = 2) =
    VoiceChannel.builder()
        .id(voiceChannelId)
        .name("VoiceChannel")
        .guild(getGuild(guildId))
        .build()!!

fun getGuild(guildId: Long = 1) = Guild.builder().id(guildId).build()!!
