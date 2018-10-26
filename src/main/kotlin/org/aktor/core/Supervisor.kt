package org.aktor.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Supervisor: ActorContext {

    private val job = Job()

    val actors = mutableListOf<Actor<*>>()

    fun stop() {
        actors.forEach { it.stop() } //first gives a chance for actors to close gracefully

        job.cancel()
        // Cancel job on activity destroy. After destroy all children jobs will be cancelled automatically
        actors.removeAll{true}
    }

    override fun <T> createActor(name: String, behavior: (T) -> Unit): Actor<T> =
        SimpleActor(this, name, behavior).also { actors.add(it) }


    fun runForAWhile(timeoutInMillis: Long, init: () -> Unit) {

        runBlocking {

            stopAfterAWhile(timeoutInMillis)

            start()

            init()

        }
    }

    fun start() {

        actors.forEach { it.start() }
    }

    private fun stopAfterAWhile(timeoutInMillis: Long) {

        scope().launch {
            Thread.sleep(timeoutInMillis)
            stop()
        }
    }

    override fun scope() = CoroutineScope(job)
}