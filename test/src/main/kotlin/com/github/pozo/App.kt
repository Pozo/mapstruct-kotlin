package com.github.pozo


fun main(args: Array<String>) {
    println("args = ${args}")
    Person("Test", "Test", 2, null)
}

@KotlinBuilder
class TestClass(var name: String)

@KotlinBuilder
data class Person(val firstName: String, val lastName: String, val age: Int, val role: Role?)

@KotlinBuilder
data class PersonTwo(val name: String, val age: Int)

@KotlinBuilder
data class Role(val id: Int, val name: String, val abbreviation: String)

@KotlinBuilder
data class RoleDto(val id: Int, val name: String, val ignoredAttr: Int?)