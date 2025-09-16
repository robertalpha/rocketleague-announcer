package model

import nl.vanalphenict.model.Announcement
import kotlin.test.Test
import kotlin.test.assertEquals


class AnnouncmentTest {

    @Test
    fun testCombine() {
        assertEquals(Announcement.HUMILIATION, Announcement.HUMILIATION.combine(Announcement.FIRST_BLOOD))
        assertEquals(Announcement.HUMILIATION, Announcement.FIRST_BLOOD.combine(Announcement.HUMILIATION))
    }

}