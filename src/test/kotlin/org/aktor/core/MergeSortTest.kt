package org.aktor.core

import assertk.assert
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

typealias SortedListMsgType = Pair<List<Int>, CompletableFuture<List<Int>>>

class MergeSortTest {

    val sortAlgo: (SortedListMsgType) -> Unit = { (list,future) ->

        val sorted: List<Int> = when(list.size) {
            0, 1 -> list
            2 -> if (list[0] < list[1]) list else list.reversed()
            else -> list
        }

        future.complete(sorted)
    }

    private fun mergeSortActorSystem(simpleList: List<Int>): List<Int> {
        val result = CompletableFuture<List<Int>>()

        val supervisor = ActorSystem.createSupervisor()
        val mergeSortActor = supervisor.createActor("root", sortAlgo)
        supervisor.runForAWhile(100) {
            mergeSortActor receive Pair(simpleList, result)
        }
        return result.get()
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


}