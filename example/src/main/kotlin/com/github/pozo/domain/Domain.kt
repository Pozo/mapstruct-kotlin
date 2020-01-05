package com.github.pozo.domain

import com.github.pozo.KotlinBuilder
import java.time.LocalDate

// dto elements

@KotlinBuilder
data class RoleDto(val id: Int, val name: String, val abbreviation: String, val ignoredAttr: Int?)

@KotlinBuilder
data class PersonDto(
    val firstName: String,
    val phone: String?,
    val birthDate: LocalDate?,
    val lastName: String,
    val age: Int,
    val role: RoleDto?
)

@KotlinBuilder
data class DivisionDto(val level: Int, val coworker: PersonDto)

@KotlinBuilder
data class ProductDto(val quality: Int, val price: Int, val division: DivisionDto)

@KotlinBuilder
data class OrderDto(val isShipped: Boolean, val sent: Boolean, val product: ProductDto)

// domain elements
@KotlinBuilder
data class Role(val id: Int, val name: String, val abbreviation: String?)

@KotlinBuilder
data class Person(val firstName: String, val lastName: String, val age: Int, val role: Role?)

@KotlinBuilder
data class Division(val level: Int, val coworker: Person)

@KotlinBuilder
data class Product(val quality: Int, val price: Int, val division: Division)

@KotlinBuilder
data class Order(val isShipped: Boolean, val sent: Boolean, val product: Product)