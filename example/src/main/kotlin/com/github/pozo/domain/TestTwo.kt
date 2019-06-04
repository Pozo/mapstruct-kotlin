package com.github.pozo.domain

import com.github.pozo.KotlinBuilder
import com.github.pozo.Test
import java.math.BigDecimal

@KotlinBuilder
data class TestTwo(val age: Int, val price: BigDecimal, val test: Test)


@KotlinBuilder
class TestThree(val age: Int, val price: BigDecimal, val test: Test)