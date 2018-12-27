package com.github.pozo


fun main(args: Array<String>) {
    println("args = ${args}")
    Person("Test", "Test", 2)
}

@KotlinBuilder
class TestClass(var name: String)

@KotlinBuilder
data class Person(val firstName: String, val lastName: String, val age: Int)

@KotlinBuilder
data class PersonTwo(val name: String, val age: Int)