package org.aktor.core

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

data class SimpleActor<T>(override val context: ActorContext, val name: String, val action: (T) -> Unit) : Actor<T> {

    private var stopped: Boolean = true

    override fun stop() {
        stopped = true
    }

    override fun receive(msg: T) {
        context.scope().launch {
            receiver.send(msg)
        }
    }

    override fun T.send() = receive(this)

    val receiver = Channel<T>(1000)

    override fun onMessage(msg: T) = action(msg)


    override fun start() {

        context.scope().launch {
            stopped = false
            receiver.consumeEach {
                onMessage(it)
            }
        }

    }

}

