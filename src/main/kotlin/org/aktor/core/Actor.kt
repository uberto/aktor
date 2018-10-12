package org.aktor.core

import kotlinx.coroutines.Job

interface Actor<T> {

    fun start()

    fun stop()

    fun onMessage(msg: T)

    fun send(msg: T): Job

}
