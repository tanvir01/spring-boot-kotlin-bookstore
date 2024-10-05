package com.tanservices.bookstore.service.impl

import com.tanservices.bookstore.domain.BookSummary
import com.tanservices.bookstore.domain.BookUpdateRequest
import com.tanservices.bookstore.domain.entity.BookEntity
import com.tanservices.bookstore.repository.AuthorRepository
import com.tanservices.bookstore.repository.BookRepository
import com.tanservices.bookstore.service.BookService
import com.tanservices.bookstore.toBookEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookServiceImpl(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) : BookService {

    override fun getBook(isbn: String): BookEntity {
        return bookRepository.findByIdOrNull(isbn) ?: throw IllegalArgumentException("Book with isbn $isbn not found")
    }

    override fun getBooks(authorId: Long?): List<BookEntity> {
        return authorId?.let { bookRepository.findByAuthorEntityId(it) } ?: bookRepository.findAll()
    }

    @Transactional
    override fun createUpdate(isbn: String, bookSummary: BookSummary): Pair<BookEntity, Boolean> {
        val normalisedBook = bookSummary.copy(isbn = isbn)
        val isExists = bookRepository.existsById(isbn)

        val author = authorRepository.findByIdOrNull(bookSummary.author.id)
        checkNotNull(author) { "Author with id ${bookSummary.author.id} not found" }

        val savedBook = bookRepository.save(normalisedBook.toBookEntity(author))
        return Pair(savedBook, !isExists)
    }

    override fun partialUpdate(isbn: String, bookUpdateRequest: BookUpdateRequest): BookEntity {
        val book = bookRepository.findByIdOrNull(isbn)
        checkNotNull(book) { "Book with isbn $isbn not found" }

        val updatedBook = book.copy(
            title = bookUpdateRequest.title ?: book.title,
            description = bookUpdateRequest.description ?: book.description,
            image = bookUpdateRequest.image ?: book.image
        )

        return bookRepository.save(updatedBook)
    }

    override fun deleteBook(isbn: String) {
        bookRepository.deleteById(isbn)
    }
}