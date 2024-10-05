package com.tanservices.bookstore.controller

import com.tanservices.bookstore.dto.AuthorDTO
import com.tanservices.bookstore.dto.AuthorUpdateRequestDTO
import com.tanservices.bookstore.service.AuthorService
import com.tanservices.bookstore.toAuthorDTO
import com.tanservices.bookstore.toAuthorEntity
import com.tanservices.bookstore.toAuthorUpdateRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/v1/authors"])
class AuthorController(private val authorService: AuthorService) {

     @PostMapping
     fun createAuthor(@RequestBody authorDTO: AuthorDTO): ResponseEntity<AuthorDTO> {
         return try {
             val createdAuthor = authorService.create(authorDTO.toAuthorEntity()).toAuthorDTO()
             ResponseEntity(createdAuthor, HttpStatus.CREATED)
         } catch (e: IllegalArgumentException) {
             ResponseEntity(HttpStatus.BAD_REQUEST)
         }
     }

    @GetMapping
    fun getAuthors(): ResponseEntity<List<AuthorDTO>> {
        val authors = authorService.findAll().map { it.toAuthorDTO() }
        return ResponseEntity(authors, HttpStatus.OK)
    }

    @GetMapping(path = ["/{id}"])
    fun getAuthorById(@PathVariable("id") id: Long): ResponseEntity<AuthorDTO> {
        val author = authorService.findById(id)?.toAuthorDTO()
        return author?.let { ResponseEntity(it, HttpStatus.OK) } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PutMapping(path = ["/{id}"])
    fun fullUpdateAuthor(@PathVariable("id") id: Long, @RequestBody authorDTO: AuthorDTO): ResponseEntity<AuthorDTO> {
        return try {
            val updatedAuthor = authorService.fullUpdate(id, authorDTO.toAuthorEntity()).toAuthorDTO()
            ResponseEntity(updatedAuthor, HttpStatus.OK)
        } catch (e: IllegalStateException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PatchMapping(path = ["/{id}"])
    fun partialUpdateAuthor(@PathVariable("id") id: Long, @RequestBody authorUpdateRequestDTO: AuthorUpdateRequestDTO): ResponseEntity<AuthorDTO> {
        return try {
            val updatedAuthor = authorService.partialUpdate(id, authorUpdateRequestDTO.toAuthorUpdateRequest()).toAuthorDTO()
            ResponseEntity(updatedAuthor, HttpStatus.OK)
        } catch (e: IllegalStateException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteAuthor(@PathVariable("id") id: Long): ResponseEntity<Unit> {
        authorService.deleteById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}