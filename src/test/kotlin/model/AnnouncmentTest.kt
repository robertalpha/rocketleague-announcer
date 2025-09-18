package model

import io.kotest.matchers.shouldBe
import nl.vanalphenict.model.Announcement
import kotlin.test.Test


class AnnouncmentTest {

    @Test
    fun testCombine() {
        Announcement.HUMILIATION shouldBe Announcement.HUMILIATION.combine(Announcement.FIRST_BLOOD)
        Announcement.HUMILIATION shouldBe Announcement.FIRST_BLOOD.combine(Announcement.HUMILIATION)
    }

}