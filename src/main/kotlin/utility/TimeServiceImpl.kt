package nl.vanalphenict.utility

import kotlin.time.Clock

class TimeServiceImpl : TimeService {
    override fun now() = Clock.System.now()
}
