package com.tanservices.bookstore.exception

class InvalidAuthorException : Exception() {
    override val message: String
        get() {
            return "Author is invalid. Author id must not be null"
        }
}