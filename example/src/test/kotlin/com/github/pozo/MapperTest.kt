package com.github.pozo

import com.github.pozo.mapper.PersonMapper
import com.github.pozo.mapper.PersonMapperImpl
import com.github.pozo.mapper.RoleMapper
import com.github.pozo.mapper.RoleMapperImpl
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MapperTest {

    private lateinit var person: Person

    private lateinit var role: Role

    private lateinit var personMapper: PersonMapper

    private lateinit var roleMapper: RoleMapper

    @Before
    fun setup() {
        personMapper = PersonMapperImpl()
        roleMapper = RoleMapperImpl()
        person = Person("Test", "Test", 2, null)
        role = Role(1, "role one", "R1")
    }

    @Test
    fun testDto() {
        val personDto = personMapper.toDto(person)
        assertEquals(person.firstName, personDto.firstName)
        assertEquals(person.lastName, personDto.lastName)
        assertNull(personDto.birthDate)
        assertNull(personDto.phone)
        assertNull(personDto.role)
    }

    @Test
    fun testPerson() {
        val personDto = personMapper.toDto(person)
        val personFromDto = personMapper.toPerson(personDto)
        assertEquals(person.firstName, personFromDto.firstName)
        assertEquals(person.lastName, personFromDto.lastName)
    }

    @Test
    fun testPersonTwo() {
        val personTwo = personMapper.toPersonTwo(person)
        assertEquals(person.firstName, personTwo.name)
        assertEquals(person.age, personTwo.age)
    }

    @Test
    fun testRoleDto() {
        val roleDto = roleMapper.toDto(role)
        assertEquals(role.id, roleDto.id)
        assertEquals(role.name, roleDto.name)
        assertNull(roleDto.ignoredAttr)
    }

    @Test
    fun testRole() {
        val roleDto = roleMapper.toDto(role)
        val roleFromDto = roleMapper.toRole(roleDto)
        assertEquals(roleFromDto.id, roleDto.id)
        assertEquals(roleFromDto.name, roleDto.name)
    }

    @Test
    fun testPersonsRoleDto() {
        val copy = person.copy(role = role)
        val personDto = personMapper.toDto(copy)
        val roleDto = roleMapper.toDto(role)

        assertNotNull(personDto.role)
        assertEquals(personDto.role, roleDto)
    }
}