package nl.vanalphenict.utility

import java.awt.Color
import kotlin.test.Test
import kotlinx.serialization.json.Json
import nl.vanalphenict.model.JsonPlayerRef
import nl.vanalphenict.model.JsonStatfeedEventData
import nl.vanalphenict.utility.ColorUtils.Companion.toHexString

class Support {

    @Test
    fun writeSomeJson() {
        println(
            Json.encodeToString(
                JsonStatfeedEventData(
                    matchGuid = "123abc",
                    eventName = "Goal",
                    type = "Goal",
                    mainTarget = JsonPlayerRef(name = "Jones", shortcut = 1, teamNum = 1),
                )
            )
        )
        println(
            Json.encodeToString(
                JsonStatfeedEventData(
                    matchGuid = "123abc",
                    eventName = "Shot",
                    type = "Shot",
                    mainTarget = JsonPlayerRef(name = "Gordon", shortcut = 2, teamNum = 0),
                )
            )
        )
        println(
            Json.encodeToString(
                JsonStatfeedEventData(
                    matchGuid = "123abc",
                    eventName = "Demolish",
                    type = "Demolition",
                    mainTarget = JsonPlayerRef(name = "Maverick", shortcut = 0, teamNum = 1),
                    secondaryTarget = JsonPlayerRef(name = "Gordon", shortcut = 2, teamNum = 0),
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
