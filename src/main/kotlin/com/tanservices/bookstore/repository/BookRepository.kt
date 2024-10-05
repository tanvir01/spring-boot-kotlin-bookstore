package com.tanservices.bookstore.repository

import com.tanservices.bookstore.domain.entity.BookEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : JpaRepository<BookEntity, String> {
    fun findByAuthorEntityId(authorId: Long): List<BookEntity>
}