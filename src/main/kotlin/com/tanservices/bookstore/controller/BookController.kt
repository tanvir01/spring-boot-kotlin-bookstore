package com.tanservices.bookstore.controller

import com.tanservices.bookstore.dto.BookSummaryDTO
import com.tanservices.bookstore.dto.BookUpdateRequestDTO
import com.tanservices.bookstore.exception.InvalidAuthorException
import com.tanservices.bookstore.service.BookService
import com.tanservices.bookstore.toBookSummary
import com.tanservices.bookstore.toBookSummaryDTO
import com.tanservices.bookstore.toBookUpdateRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/v1/books"])
class BookController(private val bookService: BookService) {

    @GetMapping
    fun getBooks(@RequestParam("author") authorId: Long?): List<BookSummaryDTO> {
        return bookService.getBooks(authorId).map { it.toBookSummaryDTO() }
    }

    @GetMapping(path = ["/{isbn}"])
    fun getBook(@PathVariable("isbn") isbn: String): ResponseEntity<BookSummaryDTO> {
        return try {
            ResponseEntity(bookService.getBook(isbn).toBookSummaryDTO(), HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping(path = ["/{isbn}"])
    fun createFullUpdateBook(@PathVariable("isbn") isbn: String, @RequestBody book: BookSummaryDTO): ResponseEntity<BookSummaryDTO> {
        try {
            val (updatedBook, isCreated) = bookService.createUpdate(isbn, book.toBookSummary())
            return ResponseEntity(updatedBook.toBookSummaryDTO(), if (isCreated) HttpStatus.CREATED else HttpStatus.OK)
        } catch (e: IllegalStateException) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: InvalidAuthorException) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PatchMapping(path = ["/{isbn}"])
    fun partialUpdateBook(@PathVariable("isbn") isbn: String, @RequestBody bookUpdateRequestDTO: BookUpdateRequestDTO): ResponseEntity<BookSummaryDTO> {
        try {
            val updatedBook = bookService.partialUpdate(isbn, bookUpdateRequestDTO.toBookUpdateRequest()).toBookSummaryDTO()
            return ResponseEntity(updatedBook, HttpStatus.OK)
        } catch (e: IllegalStateException) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping(path = ["/{isbn}"])
    fun deleteBook(@PathVariable("isbn") isbn: String): ResponseEntity<Unit> {
        bookService.deleteBook(isbn)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}