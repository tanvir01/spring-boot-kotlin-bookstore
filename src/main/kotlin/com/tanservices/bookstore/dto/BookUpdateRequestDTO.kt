package com.tanservices.bookstore.dto


data class BookUpdateRequestDTO(
    val title: String? = null,
    val description: String? = null,
    val image: String? = null,
)