package nl.vanalphenict.services

import nl.vanalphenict.model.Announcement

interface StatToAnnouncment {

    fun interpret(statMessage: nl.vanalphenict.model.StatMessage): Announcement
}