package org.aktor.core

import org.junit.jupiter.api.Test
import assertk.assert
import assertk.assertions.isEqualTo
import java.lang.StringBuilder
import java.util.Random
import kotlin.random.Random
import kotlin.random.nextInt

internal class WorkersTest {

    data class HammingDistance(val goal: String, val current: String){
        fun dist(): Int = if (goal.length != current.length) {Int.MAX_VALUE } else {goal.zip(current).filter { it.first != it.second }.count() }
        fun mutate(): HammingDistance = HammingDistance(goal, replaceRndChar(current))

        private fun replaceRndChar(current: String): String {
            val sb = StringBuilder(current)
            sb.setCharAt(Random.nextInt(0, current.length), Random.nextInt('a'.toInt() ..'z'.toInt()).toChar())
            return sb.toString()
        }
    }

    val supervisor = ActorSystem.createSupervisor()

    private val actors = (1..10).map { supervisor.createStatelessActor("actor $1", distance) }

    val distributor: DistributorActor<HammingDistance> = DistributorActor("distrib", supervisor, actors)

    private val distance: suspend Actor<HammingDistance>.(Envelope<HammingDistance>) -> Unit = {
        val cd = it.payload

        val dist = cd.dist()
        if (dist == 0) {
            println("FOUND!!!!!!!!!!!!!!!!!")
        } else {
             val new = cd.mutate()
            if (new.dist() < dist)
                new.sendTo(distributor)

        }

    }


    @Test
    fun hammingDist(){
        assert(HammingDistance("cat", "bat").dist()).isEqualTo(1)
        assert(HammingDistance("cat", "dog").dist()).isEqualTo(3)
        assert(HammingDistance("abcde", "fghil").dist()).isEqualTo(5)
        assert(HammingDistance("cat", "at").dist()).isEqualTo(Int.MAX_VALUE)
    }




}