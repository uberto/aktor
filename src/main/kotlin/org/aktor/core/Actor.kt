package org.aktor.core

interface Actor<T> {

    val name: String

    val process: suspend Actor<T>.(Envelope<T>) -> Unit

    val context: ActorContext

    fun start()

    fun stop()

    infix fun receive(m :Envelope<T>)

    fun T.sendTo(a: Actor<T>)

    fun canReceive(): Boolean

}
