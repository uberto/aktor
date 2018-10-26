package org.aktor.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

data class SimpleActor<T>(override val context: ActorContext, val name: String, val action: (T) -> Unit) : Actor<T> {

    override fun receive(msg: T) {
        context.scope.launch { receiver.send(msg) }
    }

    override fun T.to() = receive(this)

    val receiver = Channel<T>(1000)

    override fun onMessage(msg: T) = action(msg)

    var job: Job? = null

    override fun start() {
        job = context.scope.launch {
            receiver.consumeEach {
                onMessage(it)
            }
        }
    }

    override fun stop() {
        job?.cancel()
    }
}

