package com.tanservices.bookstore.service.impl

import com.tanservices.bookstore.domain.AuthorUpdateRequest
import com.tanservices.bookstore.repository.AuthorRepository
import com.tanservices.bookstore.testAuthorEntityA
import com.tanservices.bookstore.testAuthorUpdateRequestA
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
class AuthorServiceImplTest @Autowired constructor(private val underTest: AuthorServiceImpl, private val authorRepository: AuthorRepository) {

    @BeforeEach
    fun setUp() {
        authorRepository.deleteAll()
    }

    @Test
    fun `test that save persists the Author in the database`() {
        // Given
        val authorEntity = testAuthorEntityA()

        // When
        val savedAuthor = underTest.create(authorEntity)

        // Then
        assertThat(savedAuthor.id).isNotNull()
        val foundAuthor = authorRepository.findByIdOrNull(savedAuthor.id!!)
        assertThat(foundAuthor).isNotNull()
        assertThat(foundAuthor!!).usingRecursiveComparison().isEqualTo(testAuthorEntityA(id = savedAuthor.id))
    }

    @Test
    fun `test that save throws an exception when the Author id is not null`() {
        // Given
        val authorEntity = testAuthorEntityA(id = 1)

        // When
        val exception = assertThrows<IllegalArgumentException> {
            underTest.create(authorEntity)
        }

        // Then
        assertThat(exception).hasMessage("Author id must be null")
    }

    @Test
    fun `test that list returns empty list when no authors in database`() {
        // When
        val authors = underTest.findAll()

        // Then
        assertThat(authors).isEmpty()
    }

    @Test
    fun `test that list returns all authors in the database`() {
        // Given
        val savedAuthor = underTest.create(testAuthorEntityA())

        // When
        val authors = underTest.findAll()

        // Then
        assertThat(authors).hasSize(1)
        assertThat(authors).isEqualTo(listOf(savedAuthor))
    }

    @Test
    fun `test that findById returns null when author not found`() {
        // When
        val author = underTest.findById(999)

        // Then
        assertThat(author).isNull()
    }

    @Test
    fun `test that findById returns the author when found`() {
        // Given
        val savedAuthor = underTest.create(testAuthorEntityA())

        // When
        val author = underTest.findById(savedAuthor.id!!)

        // Then
        assertThat(author).isEqualTo(savedAuthor)
    }

    @Test
    fun `test that fullUpdate updates the author in the database`() {
        // Given
        val savedAuthor = underTest.create(testAuthorEntityA())
        val updatedAuthor = savedAuthor.copy(name = "Updated Name")

        // When
        val author = underTest.fullUpdate(savedAuthor.id!!, updatedAuthor)

        // Then
        assertThat(author).isEqualTo(updatedAuthor)
        val foundAuthor = authorRepository.findByIdOrNull(savedAuthor.id!!)
        assertThat(foundAuthor).isEqualTo(updatedAuthor)
    }

    @Test
    fun `test that fullUpdate throws an exception when author not found`() {
        // Given
        val updatedAuthor = testAuthorEntityA(id = 999)

        // When
        val exception = assertThrows<IllegalStateException> {
            underTest.fullUpdate(updatedAuthor.id!!, updatedAuthor)
        }

        // Then
        assertThat(exception).hasMessage("Author with id 999 not found")
    }

    @Test
    fun `test that partial update Author throws an exception when author not found`() {
        // Given
        val authorUpdateRequest = testAuthorUpdateRequestA(999)

        // When
        val exception = assertThrows<IllegalStateException> {
            underTest.partialUpdate(999, authorUpdateRequest)
        }

        // Then
        assertThat(exception).hasMessage("Author with id 999 not found")
    }

    @Test
    fun `test that partial update Author does not update Author when all fields are null`() {
        // Given
        val savedAuthor = authorRepository.save(testAuthorEntityA())

        // When
        val updatedAuthor = underTest.partialUpdate(savedAuthor.id!!, AuthorUpdateRequest())

        // Then
        assertThat(updatedAuthor).isEqualTo(savedAuthor)
    }

    @Test
    fun `test that partial update Author updates the name field`() {
        assertThatAuthorPartialUpdateIsUpdated(AuthorUpdateRequest(name = "Updated Name"))
    }

    @Test
    fun `test that partial update Author updates the age field`() {
        assertThatAuthorPartialUpdateIsUpdated(AuthorUpdateRequest(age = 25))
    }

    @Test
    fun `test that partial update Author updates the description field`() {
        assertThatAuthorPartialUpdateIsUpdated(AuthorUpdateRequest(description = "Updated Description"))
    }

    @Test
    fun `test that partial update Author updates the image field`() {
        assertThatAuthorPartialUpdateIsUpdated(AuthorUpdateRequest(image = "updated-image.jpeg"))
    }

    @Test
    fun `test that deleteById removes the author from the database`() {
        // Given
        val savedAuthor = underTest.create(testAuthorEntityA())

        // When
        underTest.deleteById(savedAuthor.id!!)

        // Then
        val foundAuthor = authorRepository.findByIdOrNull(savedAuthor.id!!)
        assertThat(foundAuthor).isNull()
    }

    @Test
    fun `test that deleteById deletes an non existing Author in database`() {
        // When
        underTest.deleteById(999)

        // Then
        val foundAuthor = authorRepository.findByIdOrNull(999)
        assertThat(foundAuthor).isNull()
    }

    private fun assertThatAuthorPartialUpdateIsUpdated(authorUpdateRequest: AuthorUpdateRequest) {
        // Given
        val existingAuthor = authorRepository.save(testAuthorEntityA())

        // When
        val updatedAuthor = underTest.partialUpdate(existingAuthor.id!!, authorUpdateRequest)

        // Then
        val foundAuthor = authorRepository.findByIdOrNull(existingAuthor.id!!)
        assertNotNull(foundAuthor)
        assertThat(foundAuthor).isEqualTo(updatedAuthor)
        assertThat(updatedAuthor.name).isEqualTo(authorUpdateRequest.name ?: existingAuthor.name)
        assertThat(updatedAuthor.age).isEqualTo(authorUpdateRequest.age ?: existingAuthor.age)
        assertThat(updatedAuthor.description).isEqualTo(authorUpdateRequest.description ?: existingAuthor.description)
        assertThat(updatedAuthor.image).isEqualTo(authorUpdateRequest.image ?: existingAuthor.image)
    }

}