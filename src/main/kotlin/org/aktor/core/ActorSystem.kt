package org.aktor.core

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


object ActorSystem: ActorContext {

    override val scope = GlobalScope

    override val actors = mutableSetOf<Actor<*>>()


    override fun <T> createActor(name: String, behavior: (T) -> Unit): Actor<T> {
        val res = SimpleActor(this, name, behavior)

        actors.add(res)
        return res
    }

    override fun sendTo(actor: Actor<String>, msg: String) {
        scope.launch {
            actor receive msg
        }
    }

    fun startSystem(timeoutInMillis: Long, init: () -> Unit) {

            runBlocking {

                launch {
                    Thread.sleep(timeoutInMillis)
                    actors.forEach { it.stop() }
                }

                actors.forEach { it.start() }

                init()
            }
    }


}