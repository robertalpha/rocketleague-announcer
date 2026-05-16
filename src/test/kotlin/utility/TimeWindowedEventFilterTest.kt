package nl.vanalphenict.utility

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class TimeWindowedEventFilterTest {

    // Define a simple data class to use as our event type T
    data class TestEvent(val id: String, val payload: String)

    private class ActionMock {
        var lastEvent: TestEvent? = null
        var count: Int = 0

        fun invoke(event: TestEvent) {
            lastEvent = event
            count++
        }

        fun reset() {
            lastEvent = null
            count = 0
        }
    }

    private val timeServiceMock: TimeServiceMock = TimeServiceMock()
    private val actionMock: ActionMock = ActionMock()
    private val moment: Instant = Instant.parse("2020-01-01T00:00:00Z")

    private val cut =
        TimeWindowedEventFilter<TestEvent, String>(
            equivalenceMethod = { it.id },
            action = { actionMock.invoke(it) },
            timeWindow = 10.seconds,
            timeService = timeServiceMock,
        )

    @BeforeTest
    fun setUp() {
        actionMock.reset()
        timeServiceMock.setTime(moment)
    }

    @Test
    fun testSingleEvent() {
        val event = TestEvent("id-1", "Hello")

        cut.process(event)
        actionMock.lastEvent shouldBeSameInstanceAs event
        actionMock.count shouldBe 1
    }

    @Test
    fun testMultipleSameEventsWithinWindow() {
        val event1 = TestEvent("id-1", "First")
        val event2 = TestEvent("id-1", "Second (Equivalent)")

        cut.process(event1)

        timeServiceMock.setTime(moment + 5.seconds)
        cut.process(event2)

        actionMock.lastEvent shouldBeSameInstanceAs event1
        actionMock.count shouldBe 1
    }

    @Test
    fun testMultipleSameEventsOutsideWindow() {
        val event1 = TestEvent("id-1", "First")
        val event2 = TestEvent("id-1", "Second")

        cut.process(event1)
        actionMock.lastEvent shouldBeSameInstanceAs event1

        timeServiceMock.setTime(moment + 11.seconds)
        cut.process(event2)
        actionMock.lastEvent shouldBeSameInstanceAs event2
        actionMock.count shouldBe 2
    }

    @Test
    fun testMultipleDifferentEventsWithinWindow() {
        val eventA = TestEvent("id-A", "Apple")
        val eventB = TestEvent("id-B", "Banana")

        cut.process(eventA)
        actionMock.lastEvent shouldBeSameInstanceAs eventA

        timeServiceMock.setTime(moment + 5.seconds)
        cut.process(eventB)
        actionMock.lastEvent shouldBeSameInstanceAs eventB
        actionMock.count shouldBe 2
    }
}
