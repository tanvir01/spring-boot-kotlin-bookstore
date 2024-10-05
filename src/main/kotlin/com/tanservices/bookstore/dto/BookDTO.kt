package com.tanservices.bookstore.dto

data class BookDTO(
    val isbn: String?,
    val title: String,
    val description: String,
    val image: String,
    val author: AuthorDTO)