package nl.vanalphenict.support

import com.janoz.discord.domain.Guild
import com.janoz.discord.domain.VoiceChannel
import kotlin.time.Duration.Companion.seconds
import nl.vanalphenict.model.BLUE
import nl.vanalphenict.model.DARK_GREY
import nl.vanalphenict.model.JsonPlayerRef
import nl.vanalphenict.model.JsonStatfeedEventData
import nl.vanalphenict.model.JsonTeam
import nl.vanalphenict.model.KillMessage
import nl.vanalphenict.model.ORANGE
import nl.vanalphenict.model.Player
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.model.Team
import nl.vanalphenict.model.parseStatfeedEvent

// ── JSON-level fixtures (for deserialization / scrubber tests) ───────────────

fun getBlueTeam(score: Int = 123) =
    JsonTeam(
        name = "Team",
        teamNum = 0,
        score = score,
        colorPrimary = "1873FF",
        colorSecondary = "050505",
    )

fun getOrangeTeam(score: Int = 123) =
    JsonTeam(
        name = "Team",
        teamNum = 1,
        score = score,
        colorPrimary = "C26418",
        colorSecondary = "E5E5E5",
    )

// ── Domain-level fixtures (for announcement / service tests) ────────────────

fun blueTeam(score: Int = 0) =
    Team(teamNum = 0, score = score, primaryColor = BLUE, secondaryColor = DARK_GREY, name = "TEAM BLUE", tag = "BLUE")

fun orangeTeam(score: Int = 0) =
    Team(teamNum = 1, score = score, primaryColor = ORANGE, secondaryColor = DARK_GREY, name = "TEAM ORANGE", tag = "ORNG")

fun playerEpic(team: Team = orangeTeam()) =
    Player(id = "Epic|12345678cafebabe12345678cafebabe|0", name = "Jones", bot = false, team = team)

fun playerSteam(team: Team = blueTeam()) =
    Player(id = "Steam|12345678901234567|0", name = "Gordon", bot = false, team = team)

fun playerSwitch(team: Team = blueTeam()) =
    Player(id = "Switch|12345678901234567890|0", name = "Mario", bot = false, team = team)

fun playerPlaystation(team: Team = orangeTeam()) =
    Player(id = "PS4|1234567890123456789|0", name = "Snake", bot = false, team = team)

fun botPlayer(team: Team = orangeTeam()) =
    Player(id = "bot|Maverick|0", name = "Maverick", bot = true, team = team)

fun demoMessage(
    attacker: Player,
    victim: Player,
    matchGUID: String = "123",
) = KillMessage(matchGUID = matchGUID, event = StatEvents.DEMOLISH, player = attacker, victim = victim)

fun statMessage(
    event: StatEvents,
    player: Player,
    matchGUID: String = "123abc",
) = StatMessage(matchGUID = matchGUID, event = event, player = player)

// ── Convenience: build a StatMessage via parseStatfeedEvent (for getEvent compat) ──

fun getEvent(event: StatEvents) =
    parseStatfeedEvent(
        JsonStatfeedEventData(
            matchGuid = "123abc",
            eventName = event.eventName,
            type = event.eventName,
            mainTarget = JsonPlayerRef(name = "Jones", shortcut = 1, teamNum = 1),
        )
    )!!

fun getMetaData() = RLAMetaData(matchGUID = "123abc", overtime = false, remaining = 300.seconds)

// ── Discord fixtures ────────────────────────────────────────────────────────

fun getVoiceChannel(guildId: Long = 1, voiceChannelId: Long = 2) =
    VoiceChannel.builder()
        .id(voiceChannelId)
        .name("VoiceChannel")
        .guild(getGuild(guildId))
        .build()!!

fun getGuild(guildId: Long = 1) = Guild.builder().id(guildId).build()!!
