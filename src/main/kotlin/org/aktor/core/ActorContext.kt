package org.aktor.core

import kotlinx.coroutines.CoroutineScope

interface ActorContext {
//    fun createActor(name: String, behavior: Actor<M,S>.(Envelope<M>) -> S): Actor<out M, out S>
    fun scope(): CoroutineScope
}