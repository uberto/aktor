package org.aktor.core

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


object ActorSystem {

    private val scope = GlobalScope

    private val actors = mutableSetOf<Actor<*>>()


    fun createActor(name: String, behavior: (String) -> Unit): Actor<String> {
        val res = SimpleActor(scope, name, behavior)

        actors.add(res)
        return res
    }

    fun sendTo(actor: Actor<String>, msg: String) {
        scope.launch {
            actor.send(msg)
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