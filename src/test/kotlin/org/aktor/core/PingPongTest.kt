package org.aktor.core

import org.junit.jupiter.api.Test

internal class PingPongTest {

    val supervisor = ActorSystem.createSupervisor()

    @Test
    fun pingPong() {

        val (a1, a2) = createMirrorActors()

        supervisor.runForAWhile(10000){
            repeat(1) {
                a1.receive(Envelope(a2, "start!"))
                Thread.sleep(10)
            }
        }


    }




    private fun createMirrorActors(): Pair<Actor<String>, Actor<String>> {

        val a1: Actor<String> = supervisor.createStatelessActor("ponger") {
            println("received ${it.payload} from ${it.sender.name}")
            "pong".sendTo(it.sender as Actor<String>)

        }


        val a2: Actor<String> = supervisor.createStatelessActor("pinger") {
            println("received ${it.payload} from ${it.sender.name}")
            "ping".sendTo(it.sender as Actor<String>)
        }
        return Pair(a2, a1)
    }


}