package org.aktor.core

import org.junit.jupiter.api.Test

internal class PingPongTest {

    val supervisor = ActorSystem.createSupervisor()

    @Test
    fun pingPong() {

        val (a1, a2) = createMirrorActors()

        supervisor.runForAWhile(10000){
            repeat(1) {
                a1.receive("start!")
                Thread.sleep(10)
            }
        }


    }


    private fun createMirrorActors(): Pair<Actor<String>, Actor<String>> {
        var a2: Actor<String>? = null

        val a1 = supervisor.createActor("ponger") { msg: String ->
            println("received $msg")
            a2?.apply { "pong".send() }
        }


        a2 = supervisor.createActor("pinger") { msg ->
            println("received $msg")
            a1.apply { "ping".send() }
        }
        return Pair(a2, a1)
    }


}