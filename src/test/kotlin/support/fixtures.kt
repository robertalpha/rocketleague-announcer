package nl.vanalphenict.support

import com.janoz.discord.domain.Guild
import com.janoz.discord.domain.VoiceChannel
import nl.vanalphenict.model.JsonClub
import nl.vanalphenict.model.JsonColor
import nl.vanalphenict.model.JsonPlayer
import nl.vanalphenict.model.JsonStatMessage
import nl.vanalphenict.model.JsonTeam
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.parseStatMessage

fun getBot(team: JsonTeam, score:Int = 123) = JsonPlayer(
    id ="Unknown|0|0",
    name = "Maverick",
    score = score,
    team = team
)

fun getPlayerSwitch(team: JsonTeam, score:Int = 123) = JsonPlayer(
    id = "Switch|12345678901234567890|0",
    name = "Mario",
    score = score,
    team = team
)

fun getPlayerPlaystation(team: JsonTeam, score:Int = 123) = JsonPlayer(
    id = "PS4|1234567890123456789|0",
    name = "Snake",
    score = score,
    team = team
)

fun getPlayerEpic(team: JsonTeam, score:Int = 123) = JsonPlayer(
    id = "Epic|12345678cafebabe12345678cafebabe|0",
    name = "Jones",
    score = score,
    team = team
)

fun getPlayerSteam(team: JsonTeam, score:Int = 123) = JsonPlayer(
    id = "Steam|12345678901234567|0",
    name = "Gordon",
    score = score,
    team = team)

fun getClubJeMoeder() = JsonClub(
    id = 2194253,
    name = "JEMOEDER",
    tag = "JM",
    accentColor = JsonColor(0, 0, 178),
    primaryColor = JsonColor(38, 38, 38)
)

fun getBlueTeam(home:Boolean = true, score:Int = 123, club:Int = 0) = JsonTeam(
    clubId = club,
    homeTeam = home,
    index = 0,
    name = "Team",
    num = 0,
    score = score,
    primaryColor = JsonColor(24, 115, 255),
    secondaryColor = JsonColor(5, 5, 5)
)

fun getOrangeTeam(home:Boolean = false, score:Int = 123, club:Int = 0) = JsonTeam(
    clubId = club,
    homeTeam = home,
    index = 1,
    name = "Team",
    num = 1,
    score = score,
    primaryColor = JsonColor(194, 100, 24),
    secondaryColor = JsonColor(229, 229, 229)
)


fun getClubTeam(club:JsonClub, index: Int, home: Boolean = false, score:Int = 123) = JsonTeam(
    clubId = club.id,
    homeTeam = home,
    index = index,
    name = club.name,
    num = index,
    score = score,
    primaryColor = club.primaryColor,
    secondaryColor = club.accentColor
)

fun getEvent(event: StatEvents) = parseStatMessage(JsonStatMessage(
    matchGUID = "123abc",
    event = event.eventName,
    player = getPlayerEpic(team = getOrangeTeam())))!!


fun getVoiceChannel(guildId:Long = 1, voiceChannelId:Long = 2) = VoiceChannel
    .builder()
    .id(voiceChannelId)
    .name("VoiceChannel")
    .guild(getGuild(guildId))
    .build()!!

fun getGuild(guildId:Long = 1) = Guild.builder().id(guildId).build()!!

