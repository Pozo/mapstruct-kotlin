package com.github.pozo.mapper

import com.github.pozo.domain.Role
import com.github.pozo.domain.RoleDto
import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper
interface RoleMapper {

    @Mappings(
        value = [Mapping(target = "ignoredAttr", ignore = true)]
    )
    fun toDto(role: Role): RoleDto

    @InheritInverseConfiguration
    fun toRole(role: RoleDto): Role
}