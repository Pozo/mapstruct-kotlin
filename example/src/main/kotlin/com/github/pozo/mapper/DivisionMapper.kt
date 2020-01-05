package com.github.pozo.mapper

import com.github.pozo.domain.Division
import com.github.pozo.domain.DivisionDto
import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper
interface DivisionMapper {

    @Mappings
    fun toDto(division: Division): DivisionDto


    @Mappings
    fun toOrder(division: Division): Division
}