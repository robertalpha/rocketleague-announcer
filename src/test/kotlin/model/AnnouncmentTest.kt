package model

import nl.vanalphenict.model.Announcement
import kotlin.test.Test
import kotlin.test.assertEquals


class AnnouncmentTest {

    @Test
    fun testCombine() {
        assertEquals(Announcement.HUMMILIATION, Announcement.HUMMILIATION.combine(Announcement.FIRST_BLOOD))
        assertEquals(Announcement.HUMMILIATION, Announcement.FIRST_BLOOD.combine(Announcement.HUMMILIATION))
    }

}