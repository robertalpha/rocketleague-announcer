package nl.vanalphenict.utility

import java.awt.Color
import kotlin.test.Test
import kotlinx.serialization.json.Json
import nl.vanalphenict.model.JsonStatMessage
import nl.vanalphenict.support.getBlueTeam
import nl.vanalphenict.support.getBot
import nl.vanalphenict.support.getClubJeMoeder
import nl.vanalphenict.support.getClubTeam
import nl.vanalphenict.support.getOrangeTeam
import nl.vanalphenict.support.getPlayerEpic
import nl.vanalphenict.support.getPlayerSteam
import nl.vanalphenict.support.getPlayerSwitch
import nl.vanalphenict.utility.ColorUtils.Companion.toHexString

class Support {

    @Test
    fun writeSomeJson() {
        println(
            Json.encodeToString(
                JsonStatMessage(
                    matchGUID = "123abc",
                    event = "Goal",
                    player = getPlayerEpic(team = getOrangeTeam()),
                )
            )
        )
        println(
            Json.encodeToString(
                JsonStatMessage(
                    matchGUID = "123abc",
                    event = "Shot",
                    player = getPlayerSteam(team = getBlueTeam()),
                )
            )
        )
        println(
            Json.encodeToString(
                JsonStatMessage(
                    matchGUID = "123abc",
                    event = "EpicSave",
                    player = getPlayerSwitch(team = getClubTeam(getClubJeMoeder(), 0)),
                )
            )
        )

        println(
            Json.encodeToString(
                JsonStatMessage(
                    matchGUID = "123abc",
                    event = "Demolition",
                    player = getBot(getOrangeTeam()),
                    victim = getPlayerSteam(getBlueTeam()),
                )
            )
        )
    }

    @Test
    fun doSomeCollorThing() {
        val grey = Color(229, 229, 229)
        val blue = Color(24, 115, 255)
        val orange = Color(194, 100, 24)
        val jemoeder1 = Color(0, 178, 0)
        val jemoeder2 = Color(255, 196, 196)

        println(grey.darker().toHexString())
        println(grey.brighter().toHexString())

        println(blue.darker().toHexString())
        println(blue.brighter().toHexString())

        println(orange.darker().toHexString())
        println(orange.brighter().toHexString())

        println(jemoeder1.darker().toHexString())
        println(jemoeder1.brighter().toHexString())

        println(jemoeder2.darker().toHexString())
        println(jemoeder2.brighter().toHexString())
    }
}
