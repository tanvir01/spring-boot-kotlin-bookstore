package com.tanservices.bookstore.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.tanservices.bookstore.*
import com.tanservices.bookstore.service.BookService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.result.StatusResultMatchersDsl

private const val BOOK_API_PATH = "/v1/books"

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest @Autowired constructor(private val mockMvc: MockMvc, @MockkBean private val bookService: BookService) {

    val objectMapper = ObjectMapper()

    @Test
    fun `test that createFullUpdateBook returns HTTP 201 when book is created`() {
        assertThatBookCreatedUpdated(true) {
            isCreated()
        }
    }

    @Test
    fun `test that createFullUpdateBook returns HTTP 200 when book is updated`() {
        assertThatBookCreatedUpdated(false) {
            isOk()
        }
    }

    @Test
    fun `test that createFullUpdateBook returns HTTP 400 when author does not exist`() {
        // Given
        val authorSummaryDTO = testAuthorSummaryDtoA(id=1)
        val bookSummaryDTO = testBookSummaryDtoA(BOOK_A_ISBN, authorSummaryDTO)
        every { bookService.createUpdate(BOOK_A_ISBN, any()) } throws IllegalStateException()

        // When & Then
        mockMvc.put("$BOOK_API_PATH/$BOOK_A_ISBN") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookSummaryDTO)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `test that createFullUpdateBook returns HTTP 500 with InvalidAuthorException when author in database does not have id`() {
        // Given
        val author = testAuthorEntityA()
        val savedBook = testBookEntityA(BOOK_A_ISBN, author)
        val authorSummaryDTO = testAuthorSummaryDtoA(id=1)
        val bookSummaryDTO = testBookSummaryDtoA(BOOK_A_ISBN, authorSummaryDTO)
        every { bookService.createUpdate(BOOK_A_ISBN, any()) } returns Pair(savedBook, true)

        // When & Then
        mockMvc.put("$BOOK_API_PATH/$BOOK_A_ISBN") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookSummaryDTO)
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun `test getBooks returns HTTP 200 with list of books`() {
        // Given
        val author = testAuthorEntityA(id = 1)
        val book = testBookEntityA(BOOK_A_ISBN, author)
        every { bookService.getBooks() } returns listOf(book)

        // When & Then
        mockMvc.get(BOOK_API_PATH).andExpect {
            status { isOk() }
            content { json(objectMapper.writeValueAsString(listOf(testBookSummaryDtoA(BOOK_A_ISBN, testAuthorSummaryDtoA(id=1)))) ) }
        }
    }

    @Test
    fun `test getBooks returns HTTP 200 with list of books for author`() {
        // Given
        val author = testAuthorEntityA(id = 1)
        val book = testBookEntityA(BOOK_A_ISBN, author)
        every { bookService.getBooks(1) } returns listOf(book)

        // When & Then
        mockMvc.get(BOOK_API_PATH) {
            param("author", "1")
        }.andExpect {
            status { isOk() }
            content { json(objectMapper.writeValueAsString(listOf(testBookSummaryDtoA(BOOK_A_ISBN, testAuthorSummaryDtoA(id=1)))) ) }
        }
    }

    @Test
    fun `test getBooks returns HTTP 200 with empty list of books for author`() {
        // Given
        every { bookService.getBooks(1) } returns emptyList()

        // When & Then
        mockMvc.get(BOOK_API_PATH) {
            param("author", "1")
        }.andExpect {
            status { isOk() }
            content { json("[]") }
        }
    }

    @Test
    fun `test getBook returns HTTP 200 with book`() {
        // Given
        val author = testAuthorEntityA(id = 1)
        val book = testBookEntityA(BOOK_A_ISBN, author)
        every { bookService.getBook(BOOK_A_ISBN) } returns book

        // When & Then
        mockMvc.get("$BOOK_API_PATH/$BOOK_A_ISBN").andExpect {
            status { isOk() }
            content { json(objectMapper.writeValueAsString(testBookSummaryDtoA(BOOK_A_ISBN, testAuthorSummaryDtoA(id=1))) ) }
        }
    }

    @Test
    fun `test getBook returns HTTP 404 when book not found`() {
        // Given
        every { bookService.getBook(BOOK_A_ISBN) } throws IllegalArgumentException()

        // When & Then
        mockMvc.get("$BOOK_API_PATH/$BOOK_A_ISBN").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `test partialUpdateBook returns HTTP 200 with updated book`() {
        // Given
        val author = testAuthorEntityA(id = 1)
        val book = testBookEntityA(BOOK_A_ISBN, author)
        val bookUpdateRequestDTO = testBookUpdateRequestDtoA()
        every { bookService.partialUpdate(BOOK_A_ISBN, any()) } returns book

        // When & Then
        mockMvc.patch("$BOOK_API_PATH/$BOOK_A_ISBN") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookUpdateRequestDTO)
        }.andExpect {
            status { isOk() }
            content { json(objectMapper.writeValueAsString(testBookSummaryDtoA(BOOK_A_ISBN, testAuthorSummaryDtoA(id=1))) ) }
        }
    }

    @Test
    fun `test partialUpdateBook returns HTTP 404 when book not found`() {
        // Given
        every { bookService.partialUpdate(BOOK_A_ISBN, any()) } throws IllegalStateException()

        // When & Then
        mockMvc.patch("$BOOK_API_PATH/$BOOK_A_ISBN") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testBookUpdateRequestDtoA())
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `test deleteBook returns HTTP 204`() {
        // Given
        every { bookService.deleteBook(BOOK_A_ISBN) } answers { }

        // When & Then
        mockMvc.delete("$BOOK_API_PATH/$BOOK_A_ISBN").andExpect {
            status { isNoContent() }
        }
    }

    private fun assertThatBookCreatedUpdated(isCreated: Boolean, statusCodeAssertion: StatusResultMatchersDsl.() -> Unit) {
        // Given
        val author = testAuthorEntityA(id=1)
        val savedBook = testBookEntityA(BOOK_A_ISBN, author)
        val authorSummaryDTO = testAuthorSummaryDtoA(id=1)
        val bookSummaryDTO = testBookSummaryDtoA(BOOK_A_ISBN, authorSummaryDTO)
        every { bookService.createUpdate(BOOK_A_ISBN, any()) } returns Pair(savedBook, isCreated)

        // When & Then
        mockMvc.put("$BOOK_API_PATH/$BOOK_A_ISBN") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookSummaryDTO)
        }.andExpect {
            status { statusCodeAssertion() }
        }

    }

}