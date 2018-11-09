package org.aktor.core

import assertk.assert
import assertk.assertions.isEqualTo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.random.Random

typealias SortedListMsgType = Pair<List<Int>, CompletableDeferred<List<Int>>>

internal class MergeSortTest {

    val supervisor = ActorSystem.createSupervisor()

    val sortAlgo: suspend Actor<SortedListMsgType>.(Envelope<SortedListMsgType>) -> Unit = {

        val actor = this
        context.scope().launch {
            val (list, future) = it.payload

            val sorted: List<Int> = when (list.size) {
                0, 1 -> list
                2 -> if (list[0] < list[1]) list else list.reversed()
                else -> {
                    val (r1, r2) = splitActors(actor, list)
                    mergeLists(r1.second.await(), r2.second.await())
                }
            }

           future.complete(sorted)
        }

    }

    private fun splitActors(sender: Actor<SortedListMsgType>, list: List<Int>): Pair<SortedListMsgType, SortedListMsgType> {
        val split = list.size / 2
        val r1 = Pair(list.subList(0, split), CompletableDeferred<List<Int>>())
        val r2 = Pair(list.subList(split, list.size), CompletableDeferred<List<Int>>())
        val a1 = supervisor.createStatelessActor("a1", sortAlgo)
        val a2 = supervisor.createStatelessActor("a2", sortAlgo)

        a1.start()
        a2.start()
        a1.receive(Envelope(sender, r1))
        a2.receive(Envelope(sender, r2))
        return Pair(r1, r2)
    }

    tailrec fun mergeLists(l: List<Int>, r: List<Int>, acc: List<Int> = listOf()): List<Int> = when {
        l.isEmpty() && r.isEmpty() -> acc
        l.isEmpty() -> mergeLists(l, listOf(), acc + r)
        r.isEmpty() -> mergeLists(listOf(), r, acc + l)
        else -> if (l.first() < r.first()){
            mergeLists(l.drop(1), r, acc + l.first())
        } else {
            mergeLists(l, r.drop(1), acc + r.first())
        }
    }


    private fun mergeSortActorSystem(simpleList: List<Int>): List<Int> {
        val result = CompletableDeferred<List<Int>>()

        val sender = supervisor.createStatelessActor<SortedListMsgType>("receiver"){}
        val mergeSortActor = supervisor.createStatelessActor("root", sortAlgo)
        supervisor.runForAWhile(30000) {
            mergeSortActor receive Envelope(sender, simpleList to result)
        }

        return runBlocking {
             result.await()
        }
    }

    @Test
    fun miniList() {

        val simpleList = listOf(4, 3)

        val result = mergeSortActorSystem(simpleList)

        assert(result).isEqualTo(listOf(3,4))
    }

    @Test
    fun superMiniList() {

        val simpleList = listOf(6)

        val result = mergeSortActorSystem(simpleList)

        assert(result).isEqualTo(listOf(6))

    }

    @Test
    fun smallList() {

        val simpleList = listOf(4, 3, 12323, 1, 234, 5262)

        val result = mergeSortActorSystem(simpleList)

        assert(result).isEqualTo(listOf(1, 3, 4, 234, 5262, 12323))

    }

    @Test
    fun bigList() {

        val bigList = mutableListOf<Int>()

        for (i in 0..10_000){
            bigList.add(Random.nextInt(0, 1_000_000))
        }

        val result = mergeSortActorSystem(bigList)

        assert(result).isEqualTo(bigList.sorted())

        println(supervisor.actors.size)

    }



}