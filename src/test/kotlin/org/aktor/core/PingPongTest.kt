package org.aktor.core

import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

internal class PingPongTest {

    @Test
    fun pingPong() {

        val (a1, a2) = createMirrorActors()

        ActorSystem.startSystem(1000) {
            repeat(1) {
                ActorSystem.sendTo(a1, "start!")
                sleep(10)
            }
        }


    }


    private fun createMirrorActors(): Pair<Actor<String>, Actor<String>> {
        var a2: Actor<String>? = null

        val a1 = ActorSystem.createActor("ponger") { msg: String ->
            println("received $msg")
            a2?.let { ActorSystem.sendTo(it, "pong") }
        }


        a2 = ActorSystem.createActor("pinger") { msg ->
            println("received $msg")
            ActorSystem.sendTo(a1, "ping")
        }
        return Pair(a2, a1)
    }


}