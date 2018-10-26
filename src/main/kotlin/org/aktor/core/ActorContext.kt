package org.aktor.core

import kotlinx.coroutines.CoroutineScope

interface ActorContext {
    fun <T> createActor(name: String, behavior: (T) -> Unit): Actor<T>
    fun scope(): CoroutineScope
}