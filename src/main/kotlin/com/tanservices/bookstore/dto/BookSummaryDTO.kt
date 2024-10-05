package com.tanservices.bookstore.dto

data class BookSummaryDTO(
    val isbn: String?,
    val title: String,
    val description: String,
    val image: String,
    val author: AuthorSummaryDTO
)