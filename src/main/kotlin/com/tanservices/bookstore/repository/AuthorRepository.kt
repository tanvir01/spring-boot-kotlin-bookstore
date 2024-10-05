package com.tanservices.bookstore.repository

import com.tanservices.bookstore.domain.entity.AuthorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository : JpaRepository<AuthorEntity, Long> {
}