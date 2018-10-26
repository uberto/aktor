package org.aktor.core

interface Actor<T> {

    val context: ActorContext

    fun start()

    fun stop()

    fun onMessage(msg: T)

    infix fun receive(msg: T)

    fun T.to()

}
