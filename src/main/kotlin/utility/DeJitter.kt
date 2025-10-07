package nl.vanalphenict.utility

import java.util.Timer
import java.util.function.Consumer
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.schedule
import kotlin.time.Duration

@OptIn(ExperimentalAtomicApi::class)
class DeJitter<T> (
    private val timeToCombine : Duration,
    private val comparator : Comparator<T>,
    private val action : Consumer<T>
) {

    private var candidate : T? = null

    private val semaphore = AtomicInt(0)

    fun add(newCandidate: T?) : Boolean {
        if (newCandidate == null) return false
        semaphore.addAndFetch(1)
        // Up semaphore
        if (candidate == null || (comparator.compare(newCandidate, candidate) > 0)) {
            candidate = newCandidate
            Timer().schedule(timeToCombine.inWholeMilliseconds ) { annoucne() }
            return true
        } else {
            annoucne()
            return false
        }
    }

    private fun annoucne() {
        if (semaphore.addAndFetch(-1) == 0) {
            val c = candidate
            candidate = null
            action.accept(c!!)

        }
    }
}