package nl.vanalphenict.model

/**
 * Home and away teams are determined by which players are sending in events. Players sending in
 * events are considered home team. Results might get unpredictable when players from both teams are
 * connected.
 */
enum class Announcement {
    KILLED_BY_BOT,          // When a bot kills a player
    OWN_GOAL,               // When a player scores an own goal
    REVENGE,                // When a player kills an opponent who killed him
    RETALIATION,            // When the home team kills an opponent who killed anyone of the home team
    MASSACRE,               // Every kill in a killstreak longer than 5
    PENTA_KILL,             // Killstreak of 5
    QUAD_KILL,              // Killstreak of 4
    TRIPLE_KILL,            // Killstreak of 3
    DOUBLE_KILL,            // Killstreak of 2
    KILL,                   // Player of the away team is killed
    KILLED,                 // Player of the home team is killed
    FIRST_BLOOD,            // First kill in the match
    WITNESS,                // Player of the home team is killed, but the team scores a goal
    COMBO_BREAKER,          // Someone broke a killstreak of at least 2
    KICK_OFF_KILL,          // Kill during kickoff
    EXTERMINATION,          // 7 demolitions = extermination
    EXTERMINATION_DOUBLE,   // 14 demolitions = second extermination
    MUTUAL_DESTRUCTION,     // Player demolishes opponent, but is demolished as well

    GOAL,                   // Goal
    GOAL_HOME,              // Goal scored by home team
    GOAL_AWAY,              // Goal scored by away team
    GOAL_BY_BOT,            // Goal scored by bot

    MATCH_START,            // Match has started

    LEFT_5,                 // 5 secconds left
    LEFT_4,                 // 4 secconds left
    LEFT_3,                 // 3 secconds left
    LEFT_2,                 // 2 secconds left
    LEFT_1,                 // 1 secconds left


    // Direct mapping from Rocket League events
    AERIAL_GOAL,
    BACKWARDS_GOAL,
    BICYCLE_GOAL,
    LONG_GOAL,
    TURTLE_GOAL,
    POOL_SHOT,
    HATTRICK,
    SAVE,
    EPIC_SAVE,
}
