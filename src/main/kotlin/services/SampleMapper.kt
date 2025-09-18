package nl.vanalphenict.services

import nl.vanalphenict.model.Announcement

class SampleMapper {

    private val mapping = mapOf<Announcement, Pair<Int, String>>(
        Announcement.KILLED_BY_BOT  to ( 10 to "718360|HUMILIATION" ),
        Announcement.OWN_GOAL       to (  9 to "718360|HUMILIATION" ),
        Announcement.RETALIATION    to (  8 to "718360|RETALIATION" ),
        Announcement.MASSACRE       to (  7 to "718360|MASSACRE" ),
        Announcement.PENTA_KILL     to (  6 to "718360|PENTA_KILL" ),
        Announcement.QUAD_KILL      to (  5 to "718360|QUAD_KILL" ) ,
        Announcement.TRIPLE_KILL    to (  4 to "718360|TRIPLE_KILL" ),
        Announcement.DOUBLE_KILL    to (  3 to "718360|DOUBLE_KILL" ),
        Announcement.FIRST_BLOOD    to (  2 to "718360|FIRST_BLOOD" )
    )

    private fun highestScore(one : Announcement, two : Announcement) : Announcement {
        return if (mapping[one]!!.first > mapping[two]!!.first)
            one
        else
            two
    }

    fun getSample(announcements: Set<Announcement>) : String? {
        val remaining = announcements.toMutableSet()
        remaining.retainAll(mapping.keys)
        if (remaining.isEmpty()) return null
        var announcement = remaining.first()
        remaining.forEach { other -> announcement = highestScore(announcement, other) }
        return mapping[announcement]!!.second
    }

}