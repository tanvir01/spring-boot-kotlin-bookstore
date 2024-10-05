package com.tanservices.bookstore.service.impl

import com.tanservices.bookstore.domain.entity.AuthorEntity
import com.tanservices.bookstore.repository.AuthorRepository
import com.tanservices.bookstore.domain.AuthorUpdateRequest
import com.tanservices.bookstore.service.AuthorService
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AuthorServiceImpl(private val authorRepository: AuthorRepository) : AuthorService {
    override fun create(authorEntity: AuthorEntity): AuthorEntity {
        require(authorEntity.id == null) { "Author id must be null" }
        return authorRepository.save(authorEntity)
    }

    override fun findAll(): List<AuthorEntity> {
        return authorRepository.findAll()
    }

    override fun findById(id: Long): AuthorEntity? {
        return authorRepository.findByIdOrNull(id)
    }

    @Transactional
    override fun fullUpdate(id: Long, authorEntity: AuthorEntity): AuthorEntity {
        check(authorRepository.existsById(id)) { "Author with id $id not found" }
        return authorRepository.save(authorEntity.copy(id = id))
    }

    @Transactional
    override fun partialUpdate(id: Long, authorUpdateRequest: AuthorUpdateRequest): AuthorEntity {
        val authorEntity = authorRepository.findByIdOrNull(id)
        checkNotNull(authorEntity) { "Author with id $id not found" }

        return authorRepository.save(authorEntity.copy(
            name = authorUpdateRequest.name ?: authorEntity.name,
            age = authorUpdateRequest.age ?: authorEntity.age,
            description = authorUpdateRequest.description ?: authorEntity.description,
            image = authorUpdateRequest.image ?: authorEntity.image
        ))
    }

    override fun deleteById(id: Long) {
        authorRepository.deleteById(id)
    }
}