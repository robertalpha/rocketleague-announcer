package nl.vanalphenict.utility

import kotlin.time.Clock
import kotlin.time.Instant

class TimeServiceMock : TimeService {
    var mockedTime: Instant = Clock.System.now()

    override fun now() = mockedTime

    fun setTime(newTime: Instant) {
        this.mockedTime = newTime
    }
}
