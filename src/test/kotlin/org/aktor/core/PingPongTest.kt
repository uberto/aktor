package org.aktor.core

import org.junit.jupiter.api.Test

internal class PingPongTest {

    val supervisor = ActorSystem.createSupervisor()

    @Test
    fun pingPong() {

        val a = createPingActor("pinger")
        val b = createPingActor("ponger")

        supervisor.runForAWhile(10000){
            a.receive(Envelope(b, Ball(0)))
        }

    }


    private fun createPingActor(name: String): Actor<Ball> =
         supervisor.createStatelessActor(name) {
            println("received ${it.payload} from ${it.sender.name}")
            it.payload.bounced().sendTo(it.sender as Actor<Ball>) }

    data class Ball (val bounce: Int) {
        fun bounced(): Ball = Ball(bounce + 1)
    }


}