package nl.vanalphenict.utility

import kotlin.time.Instant

interface TimeService {
    fun now(): Instant
}
