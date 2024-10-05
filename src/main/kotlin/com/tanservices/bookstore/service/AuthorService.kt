package com.tanservices.bookstore.service

import com.tanservices.bookstore.domain.entity.AuthorEntity
import com.tanservices.bookstore.domain.AuthorUpdateRequest

interface AuthorService {
    fun create(authorEntity: AuthorEntity): AuthorEntity
    fun findAll(): List<AuthorEntity>
    fun findById(id: Long): AuthorEntity?
    fun fullUpdate(id: Long, authorEntity: AuthorEntity): AuthorEntity
    fun partialUpdate(id: Long, authorUpdateRequest: AuthorUpdateRequest): AuthorEntity
    fun deleteById(id: Long): Unit
}