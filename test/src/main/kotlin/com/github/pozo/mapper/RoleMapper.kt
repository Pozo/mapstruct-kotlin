package com.github.pozo.mapper

import com.github.pozo.Role
import com.github.pozo.RoleDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 *
 * Created by gmribas on 15/04/19.
 */
@Mapper
interface RoleMapper {

    @Mappings(
        value = [Mapping(target = "ignoredAttr", ignore = true)]
    )
    fun toDto(role: Role): RoleDto
}