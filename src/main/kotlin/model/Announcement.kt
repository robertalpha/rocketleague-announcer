package nl.vanalphenict.model

enum class Announcement {
    KILLED_BY_BOT,  //When a bot kills a player
    OWN_GOAL,       //When a player scores an own goal
    REVENGE,        //When a player kills an opponent who killed him
    RETALIATION,    //When the home team kills an opponent who killed anyone of the home team
    MASSACRE,       //Every kill in a killstreak longer than 5
    PENTA_KILL,     //Killstreak of 5
    QUAD_KILL,      //Killstreak of 4
    TRIPLE_KILL,    //Killstreak of 3
    DOUBLE_KILL,    //Killstreak of 2
    KILL,           //Player of the away team is killed
    KILLED,         //Player of the home team is killed
    FIRST_BLOOD,    //First kill in the match
    WITNESS,        //Player of the home team is killed, but the team scores a goal
    COMBO_BREAKER,  //Someone broke a killstreak of at least 2

    EXTERMINATION,  // 7 demolitions = extermination
    EXTERMINATION_DOUBLE, // 14 demolitions = second extermination
    MUTUAL_DESTRUCTION, //Player demolishes opponent, but is demolished as well
}