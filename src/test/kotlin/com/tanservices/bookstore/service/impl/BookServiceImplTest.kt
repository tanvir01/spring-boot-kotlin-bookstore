package com.tanservices.bookstore.service.impl

import com.tanservices.bookstore.*
import com.tanservices.bookstore.domain.AuthorSummary
import com.tanservices.bookstore.domain.AuthorUpdateRequest
import com.tanservices.bookstore.domain.BookUpdateRequest
import com.tanservices.bookstore.repository.AuthorRepository
import com.tanservices.bookstore.repository.BookRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertNotNull

@SpringBootTest
@Transactional
class BookServiceImplTest @Autowired constructor(
    private val underTest: BookServiceImpl,
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository) {

    @BeforeEach
    fun setUp() {
        bookRepository.deleteAll()
        authorRepository.deleteAll()
    }

    @Test
    fun `test that createUpdate creates the book in database`() {
        // Given
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val authorSummary = AuthorSummary(savedAuthor.id!!)
        val bookSummary = testBookSummaryA(BOOK_A_ISBN, authorSummary)

        // When
        val (createdBook, isCreated) = underTest.createUpdate(BOOK_A_ISBN, bookSummary)

        // Then
        val savedBook = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertThat(savedBook).isNotNull()
        assertThat(savedBook).isEqualTo(createdBook)
        assertThat(isCreated).isTrue()
    }

    @Test
    fun `test that createUpdate updates the book in database`() {
        // Given
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))
        val authorSummary = AuthorSummary(savedAuthor.id!!)
        val bookSummary = testBookSummaryB(BOOK_A_ISBN, authorSummary)

        // When
        val (updatedBook, isCreated) = underTest.createUpdate(BOOK_A_ISBN, bookSummary)

        // Then
        val updatedBookInDb = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertThat(updatedBookInDb).isNotNull()
        assertThat(updatedBookInDb).isEqualTo(updatedBook)
        assertThat(isCreated).isFalse()
    }

    @Test
    fun `test that createUpdate throws an IllegalStateException when author does not exist`() {
        // Given
        val authorSummary = AuthorSummary(1)
        val bookSummary = testBookSummaryA(BOOK_A_ISBN, authorSummary)

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            underTest.createUpdate(BOOK_A_ISBN, bookSummary)
        }

        assertThat(exception).hasMessage("Author with id 1 not found")
    }

    @Test
    fun `test that getBooks returns list of books`() {
        // Given
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))

        // When
        val books = underTest.getBooks()

        // Then
        assertThat(books).hasSize(1)
        assertThat(books[0]).isEqualTo(savedBook)
    }

    @Test
    fun `test that getBooks returns empty list when no books in database`() {
        // When
        val books = underTest.getBooks()

        // Then
        assertThat(books).isEmpty()
    }

    @Test
    fun `test that getBooks returns list of books by author`() {
        // Given
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))

        // When
        val books = underTest.getBooks(savedAuthor.id)

        // Then
        assertThat(books).hasSize(1)
        assertThat(books[0]).isEqualTo(savedBook)
    }

    @Test
    fun `test that getBooks returns empty list when no books by author in database`() {
        // Given
        val savedAuthor = authorRepository.save(testAuthorEntityA())

        // When
        val books = underTest.getBooks(savedAuthor.id)

        // Then
        assertThat(books).isEmpty()
    }

    @Test
    fun `test that getBooks returns empty list when author is different`() {
        // Given
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))

        // When
        val books = underTest.getBooks(savedAuthor.id!! + 1)

        // Then
        assertThat(books).isEmpty()
    }

    @Test
    fun `test that getBook returns book`() {
        // Given
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))

        // When
        val book = underTest.getBook(BOOK_A_ISBN)

        // Then
        assertThat(book).isEqualTo(savedBook)
    }

    @Test
    fun `test that getBook throws an IllegalArgumentException when book does not exist`() {
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            underTest.getBook(BOOK_A_ISBN)
        }

        assertThat(exception).hasMessage("Book with isbn $BOOK_A_ISBN not found")
    }

    @Test
    fun `test that partialUpdate throws an IllegalArgumentException when book does not exist`() {
        // When & Then
        val exception = assertThrows<IllegalStateException> {
            underTest.partialUpdate(BOOK_A_ISBN, testBookUpdateRequestA())
        }

        assertThat(exception).hasMessage("Book with isbn $BOOK_A_ISBN not found")
    }

    @Test
    fun `test that partialUpdate updates the book in database`() {
        // Given
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))
        val bookUpdateRequest = testBookUpdateRequestA()

        // When
        val updatedBook = underTest.partialUpdate(BOOK_A_ISBN, bookUpdateRequest)

        // Then
        val updatedBookInDb = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertThat(updatedBookInDb).isNotNull()
        assertThat(updatedBookInDb).isEqualTo(updatedBook)
    }

    @Test
    fun `test that partialUpdate updates the book title in database`() {
        assertThatBookPartialUpdateIsUpdated(BookUpdateRequest(title = "New Title"))
    }

    @Test
    fun `test that partialUpdate updates the book description in database`() {
        assertThatBookPartialUpdateIsUpdated(BookUpdateRequest(description = "New Description"))
    }

    @Test
    fun `test that partialUpdate updates the book image in database`() {
        assertThatBookPartialUpdateIsUpdated(BookUpdateRequest(image = "new-image.jpeg"))
    }

    @Test
    fun `test that deleteBook deletes the book in database`() {
        // Given
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))

        // When
        underTest.deleteBook(BOOK_A_ISBN)

        // Then
        val bookInDb = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertThat(bookInDb).isNull()
    }

    @Test
    fun `test that deleteBook deletes an non existing book in database`() {
        // When & Then
        underTest.deleteBook(BOOK_A_ISBN)

        val bookInDb = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertThat(bookInDb).isNull()
    }

    private fun assertThatBookPartialUpdateIsUpdated(bookUpdateRequest: BookUpdateRequest) {
        // Given
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))

        // When
        val updatedBook = underTest.partialUpdate(BOOK_A_ISBN, bookUpdateRequest)

        // Then
        val updatedBookInDb = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertNotNull(updatedBookInDb)
        assertThat(updatedBookInDb).isEqualTo(updatedBook)
        assertThat(updatedBookInDb.title).isEqualTo(bookUpdateRequest.title ?: savedBook.title)
        assertThat(updatedBookInDb.description).isEqualTo(bookUpdateRequest.description ?: savedBook.description)
        assertThat(updatedBookInDb.image).isEqualTo(bookUpdateRequest.image ?: savedBook.image)
    }

}