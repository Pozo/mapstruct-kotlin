package com.github.pozo.domain

import com.github.pozo.KotlinBuilder

interface Bar

@KotlinBuilder
data class Foo<T : Bar, K : String>(
    val bar: T,
    val kar: K
) {

    fun bar(k: K): T {
        return bar
    }
}

@KotlinBuilder
data class Light(
    val bar: Bar,
    val lol: String
)