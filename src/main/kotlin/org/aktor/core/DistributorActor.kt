package org.aktor.core

import kotlinx.coroutines.yield
import java.lang.Thread.yield
import java.util.Random
import kotlin.random.Random

data class DistributorActor<T>(override val name: String, override val context: ActorContext, val actors: List<Actor<T>>) : Actor<T> by StatelessActor(context, name, {

    val msg = it

    while(true){

        val x = actors[Random.nextInt(0, actors.size)]

        if ( x.canReceive() ) {
            x.receive(msg)
            break
        }

        yield()

    }


})







