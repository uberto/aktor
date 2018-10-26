package org.aktor.core

import assertk.assert
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

typealias SortedListMsgType = Pair<List<Int>, CompletableFuture<List<Int>>>

class MergeSortTest {

    val supervisor = ActorSystem.createSupervisor()

    fun sortAlgo(msg: SortedListMsgType) {

        val (list, future) = msg

        val sorted: List<Int> = when(list.size) {
            0, 1 -> list
            2 -> if (list[0] < list[1]) list else list.reversed()
            else -> {
                val (r1, r2) = splitActors(list)

                mergeLists(r1.second.get(10, TimeUnit.SECONDS), r2.second.get(10, TimeUnit.SECONDS))
            }
        }

        future.complete(sorted)
    }

    private fun splitActors(list: List<Int>): Pair<Pair<List<Int>, CompletableFuture<List<Int>>>, Pair<List<Int>, CompletableFuture<List<Int>>>> {
        val split = list.size / 2
        val r1 = Pair(list.subList(0, split), CompletableFuture<List<Int>>())
        val r2 = Pair(list.subList(split, list.size), CompletableFuture<List<Int>>())
        val a1 = supervisor.createActor("a1", ::sortAlgo)
        val a2 = supervisor.createActor("a2", ::sortAlgo)

        a1.start()
        a1.receive(r1)
        a2.start()
        a2.receive(r2)
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
        val result = CompletableFuture<List<Int>>()

        val mergeSortActor = supervisor.createActor("root", ::sortAlgo)
        supervisor.runForAWhile(10000) {
            mergeSortActor receive Pair(simpleList, result)
        }
        return result.get(10000, TimeUnit.MILLISECONDS)
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

        for (i in 0..15){
            bigList.add(Random.nextInt(0, 10000000))
        }

        val result = mergeSortActorSystem(bigList)

        assert(result).isEqualTo(bigList.sorted())

        println(supervisor.actors.size)

    }


}