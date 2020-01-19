package com.github.pozo.mapper

import com.github.pozo.domain.Product
import com.github.pozo.domain.ProductDto
import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper
interface ProductMapper {

    @Mappings
    fun toDto(product: Product): ProductDto


    @Mappings
    fun toProduct(product: ProductDto): Product
}