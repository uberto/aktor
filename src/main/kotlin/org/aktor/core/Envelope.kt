package org.aktor.core

data class Envelope<T> (val sender:Actor<*>, val payload: T)
