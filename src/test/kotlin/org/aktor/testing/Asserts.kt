package org.aktor.testing

import org.opentest4j.AssertionFailedError


infix fun Any.eq(expected: Any?): Unit = when(this) {
    expected -> Unit
    else -> throw AssertionFailedError("Comparison failed", expected, this)
}

infix fun Any.`==`(expected: Any?): Unit = this eq expected

infix fun Any.`≡`(expected: Any?): Unit = this eq expected

infix fun Any.`同`(expected: Any?): Unit = this eq expected