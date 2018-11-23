package org.aktor.testing

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.support.AnnotationConsumer
import java.util.*
import java.util.stream.Stream

@ArgumentsSource(TabularArgumentsProvider::class)
annotation class TabularSource(
        /**
         * Blabla
         */
        vararg val values: String)


class TabularArgumentsProvider : ArgumentsProvider, AnnotationConsumer<TabularSource> {
    lateinit var annotation: TabularSource

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =

        this.annotation.values
                .map { it.split("|").map { it.trim() } .toTypedArray() }
                .map { Arguments.of(*it) }
                .stream()

    override fun accept(annotation: TabularSource) {
        this.annotation = annotation

        //if not tabular raise an exception

    }

}
