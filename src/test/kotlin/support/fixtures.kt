package support

import nl.vanalphenict.model.Club
import nl.vanalphenict.model.Color
import nl.vanalphenict.model.Player
import nl.vanalphenict.model.Team

fun getBot(team: Team, score:Int = 123): Player {
    return Player("Unknown|0|0", "Maverick", score, team)
}

fun getPlayerSwitch(team: Team, score:Int = 123): Player {
    return Player("Switch|12345678901234567890|0", "Mario", score, team)
}

fun getPlayerPlaystation(team: Team, score:Int = 123): Player {
    return Player("PS4|1234567890123456789|0", "Snake", score, team)
}

fun getPlayerEpic(team: Team, score:Int = 123): Player {
    return Player("Epic|12345678cafebabe12345678cafebabe|0", "Jones", score, team)
}

fun getPlayerSteam(team: Team, score:Int = 123): Player {
    return Player("Steam|12345678901234567|0", "Gordon", score, team)
}

fun getClubJeMoeder(): Club {
    return Club( 2194253,"JM", "JEMOEDER", Color(0, 0, 178), Color(38, 38, 38),)
}

fun getBlueTeam(home:Boolean = true, score:Int = 123, club:Int = 0): Team {
    return Team(club,home,0,"Team",0,score,Color(24, 115, 255),Color(5, 5, 5))
}

fun getOrangeTeam(home:Boolean = false, score:Int = 123, club:Int = 0): Team {
    return Team(club,home,1,"Team",1,score,Color(194, 100, 24),Color(229, 229, 229))
}

fun getClubTeam(club:Club, index: Int, home: Boolean = false, score:Int = 123): Team {
    return Team(club.id, home, index, club.name, index,score,club.primaryColor,club.accentColor)
}
