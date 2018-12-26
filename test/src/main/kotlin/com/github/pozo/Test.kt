package com.github.pozo

import java.time.LocalDate

@KotlinBuilder
data class Test(val age: Int, val meh: Person)

@KotlinBuilder
data class PersonDto(val firstName: String, val lastName: String, val phone: String, val birthdate: LocalDate)