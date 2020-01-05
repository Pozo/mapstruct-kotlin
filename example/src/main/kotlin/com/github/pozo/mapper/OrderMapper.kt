package com.github.pozo.mapper

import com.github.pozo.domain.Order
import com.github.pozo.domain.OrderDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper
interface OrderMapper {

    @Mappings(
        value = [
            Mapping(source = "shipped", target = "isShipped")
        ]
    )
    fun toDto(order: Order): OrderDto


    @Mappings(
        value = [
            Mapping(source = "shipped", target = "isShipped")
        ]
    )
    fun toOrder(order: OrderDto): Order
}