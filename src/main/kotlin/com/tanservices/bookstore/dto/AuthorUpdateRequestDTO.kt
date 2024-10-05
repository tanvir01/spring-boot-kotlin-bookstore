package com.tanservices.bookstore.dto

data class AuthorUpdateRequestDTO(
    val id: Long?,
    val name: String?,
    val age: Int?,
    val description: String?,
    val image: String?)