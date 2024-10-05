package com.tanservices.bookstore

import com.tanservices.bookstore.domain.AuthorSummary
import com.tanservices.bookstore.domain.entity.AuthorEntity
import com.tanservices.bookstore.domain.AuthorUpdateRequest
import com.tanservices.bookstore.domain.BookSummary
import com.tanservices.bookstore.domain.BookUpdateRequest
import com.tanservices.bookstore.domain.entity.BookEntity
import com.tanservices.bookstore.dto.*
import com.tanservices.bookstore.exception.InvalidAuthorException

fun AuthorEntity.toAuthorDTO() = AuthorDTO(
    id = this.id,
    name = this.name,
    age = this.age,
    description = this.description,
    image = this.image
)

fun AuthorDTO.toAuthorEntity() = AuthorEntity(
    id = this.id,
    name = this.name,
    age = this.age,
    description = this.description,
    image = this.image
)

fun AuthorUpdateRequestDTO.toAuthorUpdateRequest() = AuthorUpdateRequest(
    id = this.id,
    name = this.name,
    age = this.age,
    description = this.description,
    image = this.image
)

fun AuthorSummaryDTO.toAuthorSummary() = AuthorSummary(
    id = this.id,
    name = this.name,
    image = this.image
)

fun AuthorEntity.toAuthorSummaryDTO(): AuthorSummaryDTO {
    val authorId = this.id ?: throw InvalidAuthorException()
    return AuthorSummaryDTO(
        id = authorId,
        name = this.name,
        image = this.image
    )
}

fun BookSummary.toBookEntity(author: AuthorEntity) = BookEntity(
    isbn = this.isbn,
    title = this.title,
    description = this.description,
    image = this.image,
    authorEntity = author
)

fun BookSummaryDTO.toBookSummary() = BookSummary(
    isbn = this.isbn,
    title = this.title,
    description = this.description,
    image = this.image,
    author = this.author.toAuthorSummary()
)

fun BookEntity.toBookSummaryDTO() = BookSummaryDTO(
    isbn = this.isbn,
    title = this.title,
    description = this.description,
    image = this.image,
    author = this.authorEntity.toAuthorSummaryDTO()
)

fun BookUpdateRequestDTO.toBookUpdateRequest() = BookUpdateRequest(
    title = this.title,
    description = this.description,
    image = this.image
)