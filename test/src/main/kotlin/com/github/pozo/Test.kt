package com.github.pozo

import java.math.BigDecimal
import java.time.LocalDate

@KotlinBuilder
data class Test(val age: Int, val price: BigDecimal)
// TODO currently its not working
//data class Test(val age: Int, val meh: Person)

@KotlinBuilder
data class PersonDto(val firstName: String, val lastName: String, val phone: String, val birthdate: LocalDate)