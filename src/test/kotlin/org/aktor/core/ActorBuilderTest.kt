package org.aktor.core

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

internal class ActorBuilderTest {

    @Test
    fun pingPong() {

        val (a1, a2) = createMirrorActors()

        runBlocking {

            launch {
                sleep(1000)
                a1.stop()
                a2.stop()
            }

            a1.start()
            a2.start()

            repeat(10) {
                ActorBuilder.sendTo(a1, "start!")
                sleep(10)
            }
        }

    }




    private fun createMirrorActors(): Pair<Actor<String>, Actor<String>> {
        var a2: Actor<String>? = null

        val a1 = ActorBuilder.createActor("ponger") { msg ->
            println("received $msg")
            a2?.let { ActorBuilder.sendTo(it, "pong") }
        }


        a2 = ActorBuilder.createActor("pinger") { msg ->
            println("received $msg")
            ActorBuilder.sendTo(a1, "ping")
        }
        return Pair(a2, a1)
    }


}