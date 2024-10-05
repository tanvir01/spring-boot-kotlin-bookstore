package com.tanservices.bookstore.service

import com.tanservices.bookstore.domain.BookSummary
import com.tanservices.bookstore.domain.BookUpdateRequest
import com.tanservices.bookstore.domain.entity.BookEntity

interface BookService {
    fun getBook(isbn: String): BookEntity
    fun getBooks(authorId: Long?=null): List<BookEntity>
    fun createUpdate(isbn: String, bookSummary: BookSummary): Pair<BookEntity, Boolean>
    fun partialUpdate(isbn: String, bookUpdateRequest: BookUpdateRequest): BookEntity
    fun deleteBook(isbn: String): Unit
}