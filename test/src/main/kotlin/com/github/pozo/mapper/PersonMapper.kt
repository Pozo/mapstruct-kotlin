package com.github.pozo.mapper

import com.github.pozo.Person
import com.github.pozo.PersonDto
import com.github.pozo.PersonTwo
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(uses = [RoleMapper::class])
interface PersonMapper {

    @Mappings(
        value = [
            Mapping(target = "role", ignore = true),
            Mapping(target = "phone", ignore = true),
            Mapping(target = "birthDate", ignore = true)
        ]
    )
    fun toDto(person: Person): PersonDto

    @Mappings(
        value = [Mapping(source = "firstName", target = "name")]
    )
    fun toPersonTwo(person: Person): PersonTwo
}