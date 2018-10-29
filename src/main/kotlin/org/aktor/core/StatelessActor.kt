package org.aktor.core

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

data class StatelessActor<T>(override val context: ActorContext, override val name: String, override val process: Actor<T>.(Envelope<T>) -> Unit) : Actor<T> {

    private var stopped: Boolean = true

    val inputChannel = Channel<Envelope<T>>(1000)


    override fun stop() {
        stopped = true
    }

    override fun receive(msg: Envelope<T>) {
        context.scope().launch {
            inputChannel.send(msg)
        }
    }

    override fun T.sendTo(a: Actor<T>) {
        a.receive(Envelope(this@StatelessActor, this))
    }

    override fun start() {

        context.scope().launch {
            stopped = false
            inputChannel.consumeEach {
                process(it)
            }
        }

    }

}

