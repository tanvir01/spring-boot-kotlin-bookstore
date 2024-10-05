package com.tanservices.bookstore

import com.tanservices.bookstore.domain.AuthorSummary
import com.tanservices.bookstore.domain.entity.AuthorEntity
import com.tanservices.bookstore.domain.AuthorUpdateRequest
import com.tanservices.bookstore.domain.BookSummary
import com.tanservices.bookstore.domain.BookUpdateRequest
import com.tanservices.bookstore.domain.entity.BookEntity
import com.tanservices.bookstore.dto.*

const val BOOK_A_ISBN = "978-091-369853-9242"

fun testAuthorDtoA(id: Long? = null) = AuthorDTO(
    id = id,
    name = "John Doe",
    age = 30,
    description = "A great author",
    image = "author-image.jpeg"
)

fun testAuthorEntityA(id: Long? = null) = AuthorEntity(
    id = id,
    name = "John Doe",
    age = 30,
    description = "A great author",
    image = "author-image.jpeg"
)

fun testAuthorUpdateRequestDtoA(id: Long? = null) = AuthorUpdateRequestDTO(
    id = id,
    name = "John Doe",
    age = 30,
    description = "A great author",
    image = "author-image.jpeg"
)

fun testAuthorUpdateRequestA(id: Long? = null) = AuthorUpdateRequest(
    id = id,
    name = "Jane Doe",
    age = 25,
    description = "A great author",
    image = "author-image.jpeg"
)

fun testAuthorSummaryDtoA(id: Long) = AuthorSummaryDTO(
    id = id,
    name = "John Doe",
    image = "author-image.jpeg"
)

fun testAuthorSummaryA(id: Long) = AuthorSummary(
    id = id,
    name = "John Doe",
    image = "author-image.jpeg"
)

fun testBookEntityA(isbn: String, author: AuthorEntity) = BookEntity(
    isbn = isbn,
    title = "A great book",
    description = "A great book description",
    image = "book-image.jpeg",
    authorEntity = author
)

fun testBookSummaryDtoA(isbn: String, authorSummaryDTO: AuthorSummaryDTO) = BookSummaryDTO(
    isbn = isbn,
    title = "A great book",
    description = "A great book description",
    image = "book-image.jpeg",
    author = authorSummaryDTO
)

fun testBookSummaryA(isbn: String, author: AuthorSummary) = BookSummary(
    isbn = isbn,
    title = "A great book",
    description = "A great book description",
    image = "book-image.jpeg",
    author = author
)

fun testBookSummaryB(isbn: String, author: AuthorSummary) = BookSummary(
    isbn = isbn,
    title = "Another great book",
    description = "Another great book description",
    image = "book-image.jpeg",
    author = author
)

fun testBookUpdateRequestDtoA() = BookUpdateRequestDTO(
    title = "Another great book",
    description = "Another great book description",
    image = "another-book-image.jpeg",
)

fun testBookUpdateRequestA() = BookUpdateRequest(
    title = "Another great book",
    description = "Another great book description",
    image = "another-book-image.jpeg",
)