package com.tanservices.bookstore.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "authors")
data class AuthorEntity(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_id_seq")
    val id: Long?,

    @Column(name = "name")
    val name: String,

    @Column(name = "age")
    val age: Int,

    @Column(name = "description")
    val description: String,

    @Column(name = "image")
    val image: String,

    @OneToMany(mappedBy = "authorEntity", cascade = [CascadeType.REMOVE])
    val bookEntities: List<BookEntity> = emptyList()

)