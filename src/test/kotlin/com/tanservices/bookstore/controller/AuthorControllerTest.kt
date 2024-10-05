package com.tanservices.bookstore.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.tanservices.bookstore.service.AuthorService
import com.tanservices.bookstore.testAuthorDtoA
import com.tanservices.bookstore.testAuthorEntityA
import com.tanservices.bookstore.testAuthorUpdateRequestDtoA
import io.mockk.every
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

private const val AUTHOR_API_PATH = "/v1/authors"

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest @Autowired constructor(private val mockMvc: MockMvc, @MockkBean private val authorService: AuthorService) {

    val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        every {
            authorService.create(any())
        } answers {
            firstArg()
        }
    }

    @Test
    fun `test that create Author saves the Author`() {
        // When
        mockMvc.post(AUTHOR_API_PATH) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorDtoA())
        }

        // Then
        verify { authorService.create(testAuthorEntityA()) }
    }

    @Test
    fun `test that create Author returns a HTTP 201 status on a successful create`() {
        // When & Then
        mockMvc.post(AUTHOR_API_PATH) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorDtoA())
        }.andExpect {
            status { isCreated() }
        }
    }
    
    @Test
    fun `test that create Author returns a HTTP 400 status when IllegalArgumentException is thrown`() {
        // Given
        every {
            authorService.create(any())
        } throws IllegalArgumentException()

        // When & Then
        mockMvc.post(AUTHOR_API_PATH) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorDtoA())
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `test that get Authors returns a HTTP 200 status and empty list when no authors in database`() {
        // Given
        every {
            authorService.findAll()
        } answers {
            emptyList()
        }

        // When & Then
        mockMvc.get(AUTHOR_API_PATH) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { json("[]") }
        }
    }

    @Test
    fun `test that get Authors returns a HTTP 200 status and list of authors when authors in database`() {
        // Given
        every {
            authorService.findAll()
        } answers {
            listOf(testAuthorEntityA(1))
        }

        // When & Then
        mockMvc.get(AUTHOR_API_PATH) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", equalTo(1)) }
            content { jsonPath("$[0].name", equalTo("John Doe")) }
            content { jsonPath("$[0].age", equalTo(30)) }
            content { jsonPath("$[0].description", equalTo("A great author")) }
            content { jsonPath("$[0].image", equalTo("author-image.jpeg")) }

        }
    }

    @Test
    fun `test that get Author by id returns a HTTP 404 status when author does not exist in database`() {
        // Given
        every {
            authorService.findById(1)
        } answers {
            null
        }

        // When & Then
        mockMvc.get("$AUTHOR_API_PATH/1") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `test that get Author by id returns a HTTP 200 status and author when author exists in database`() {
        // Given
        every {
            authorService.findById(1)
        } answers {
            testAuthorEntityA(1)
        }

        // When & Then
        mockMvc.get("$AUTHOR_API_PATH/1") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", equalTo(1)) }
            content { jsonPath("$.name", equalTo("John Doe")) }
            content { jsonPath("$.age", equalTo(30)) }
            content { jsonPath("$.description", equalTo("A great author")) }
            content { jsonPath("$.image", equalTo("author-image.jpeg")) }
        }
    }

    @Test
    fun `test that full update Author return HTTP 200 and updated Author on successful call`() {
        // Given
        every {
            authorService.fullUpdate(any(), any())
        } answers {
            secondArg()
        }

        // When & Then
        mockMvc.put("$AUTHOR_API_PATH/1") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorDtoA(id=1))
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", equalTo(1)) }
            content { jsonPath("$.name", equalTo("John Doe")) }
            content { jsonPath("$.age", equalTo(30)) }
            content { jsonPath("$.description", equalTo("A great author")) }
            content { jsonPath("$.image", equalTo("author-image.jpeg")) }
        }
    }

    @Test
    fun `test that full update Author return HTTP 404 when author does not exist in database`() {
        // Given
        every {
            authorService.fullUpdate(any(), any())
        } throws(IllegalStateException("Author with id 1 not found"))

        // When & Then
        mockMvc.put("$AUTHOR_API_PATH/1") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorDtoA(id=1))
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `test that partial update Author return HTTP 200 and updated Author on successful call`() {
        // Given
        every {
            authorService.partialUpdate(any(), any())
        } answers {
            testAuthorEntityA(id = 999)
        }

        // When & Then
        mockMvc.patch("$AUTHOR_API_PATH/1") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorUpdateRequestDtoA(id=1))
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", equalTo(999)) }
            content { jsonPath("$.name", equalTo("John Doe")) }
            content { jsonPath("$.age", equalTo(30)) }
            content { jsonPath("$.description", equalTo("A great author")) }
            content { jsonPath("$.image", equalTo("author-image.jpeg")) }
        }
    }

    @Test
    fun `test that partial update Author return HTTP 404 when author does not exist in database`() {
        // Given
        every {
            authorService.partialUpdate(any(), any())
        } throws(IllegalStateException("Author with id 1 not found"))

        // When & Then
        mockMvc.patch("$AUTHOR_API_PATH/1") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorUpdateRequestDtoA(id=1))
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `test that delete Author return HTTP 204 on successful delete`() {
        // Given
        every { authorService.deleteById(1) } answers { }

        // When & Then
        mockMvc.delete("$AUTHOR_API_PATH/1") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }

}