package nl.vanalphenict.support

import com.janoz.discord.domain.Guild
import com.janoz.discord.domain.VoiceChannel
import nl.vanalphenict.model.Club
import nl.vanalphenict.model.Color
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.Player
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.model.Team

fun getBot(team: Team, score:Int = 123) = Player(
    id ="Unknown|0|0",
    name = "Maverick",
    score = score,
    team = team
)

fun getPlayerSwitch(team: Team, score:Int = 123) = Player(
    id = "Switch|12345678901234567890|0",
    name = "Mario",
    score = score,
    team = team
)

fun getPlayerPlaystation(team: Team, score:Int = 123) = Player(
    id = "PS4|1234567890123456789|0",
    name = "Snake",
    score = score,
    team = team
)

fun getPlayerEpic(team: Team, score:Int = 123) = Player(
    id = "Epic|12345678cafebabe12345678cafebabe|0",
    name = "Jones",
    score = score,
    team = team
)

fun getPlayerSteam(team: Team, score:Int = 123) = Player(
    id = "Steam|12345678901234567|0",
    name = "Gordon",
    score = score,
    team = team)

fun getClubJeMoeder() = Club(
    id = 2194253,
    name = "JEMOEDER",
    tag = "JM",
    accentColor = Color(0, 0, 178),
    primaryColor = Color(38, 38, 38)
)

fun getBlueTeam(home:Boolean = true, score:Int = 123, club:Int = 0) = Team(
    clubId = club,
    homeTeam = home,
    index = 0,
    name = "Team",
    num = 0,
    score = score,
    primaryColor = Color(24, 115, 255),
    secondaryColor = Color(5, 5, 5)
)

fun getOrangeTeam(home:Boolean = false, score:Int = 123, club:Int = 0) = Team(
    clubId = club,
    homeTeam = home,
    index = 1,
    name = "Team",
    num = 1,
    score = score,
    primaryColor = Color(194, 100, 24),
    secondaryColor = Color(229, 229, 229)
)


fun getClubTeam(club:Club, index: Int, home: Boolean = false, score:Int = 123) = Team(
    clubId = club.id,
    homeTeam = home,
    index = index,
    name = club.name,
    num = index,
    score = score,
    primaryColor = club.primaryColor,
    secondaryColor = club.accentColor
)

fun getEvent(event: Events) = StatMessage(
    matchGUID = "123abc",
    event = event.eventName,
    player = getPlayerEpic(team = getOrangeTeam())
)

fun getVoiceChannel(guildId:Long = 1, voiceChannelId:Long = 2) = VoiceChannel
    .builder()
    .id(voiceChannelId)
    .name("VoiceChannel")
    .guild(getGuild(guildId))
    .build()

fun getGuild(guildId:Long = 1) = Guild.builder().id(guildId).build()

