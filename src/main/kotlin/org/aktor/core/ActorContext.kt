package org.aktor.core

import kotlinx.coroutines.CoroutineScope

interface ActorContext {
    val scope: CoroutineScope
    val actors: MutableSet<Actor<*>>
    fun <T> createActor(name: String, behavior: (T) -> Unit): Actor<T>
    fun sendTo(actor: Actor<String>, msg: String)
}