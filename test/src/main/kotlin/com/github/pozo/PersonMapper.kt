package com.github.pozo

import org.mapstruct.Mapper

@Mapper
interface PersonMapper {

    fun map(person: Person): PersonDto
}