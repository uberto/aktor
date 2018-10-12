package org.aktor.core

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


object ActorBuilder {

    private val scope = GlobalScope

    private val channels = mutableSetOf<Actor<*>>()


    fun createActor(name: String, behavior: (String) -> Unit): Actor<String> {
        val res = SimpleActor(scope, name, behavior)

        channels.add(res)
        return res
    }

    fun sendTo(actor: Actor<String>, msg: String) {
        scope.launch {
            actor.send(msg)
        }
    }

}