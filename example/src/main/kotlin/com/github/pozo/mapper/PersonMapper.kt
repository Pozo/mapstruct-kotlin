package com.github.pozo.mapper

import com.github.pozo.domain.Person
import com.github.pozo.domain.PersonDto
import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(uses = [RoleMapper::class])
interface PersonMapper {

    @Mappings(
        value = [
            Mapping(target = "role", ignore = true),
            Mapping(target = "phone", ignore = true),
            Mapping(target = "birthDate", ignore = true),
            Mapping(target = "role.id", source = "role.id"),
            Mapping(target = "role.name", source = "role.name")
        ]
    )
    fun toDto(person: Person): PersonDto

    @Mappings(
        value = [
            Mapping(target = "age", ignore = true),
            Mapping(target = "role.abbreviation", ignore = true)
        ]
    )
    @InheritInverseConfiguration
    fun toPerson(person: PersonDto): Person

}