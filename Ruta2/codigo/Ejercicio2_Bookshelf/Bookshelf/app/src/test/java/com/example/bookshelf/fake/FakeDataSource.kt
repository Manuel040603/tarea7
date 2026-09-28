package com.example.bookshelf.fake

import com.example.bookshelf.model.Book
import com.example.bookshelf.model.BooksSearchResponse
import com.example.bookshelf.model.ImageLinks
import com.example.bookshelf.model.VolumeDetail
import com.example.bookshelf.model.VolumeInfo
import com.example.bookshelf.model.VolumeSummary

object FakeDataSource {

    val searchResponse = BooksSearchResponse(
        items = listOf(VolumeSummary("1"), VolumeSummary("2"), VolumeSummary("3"))
    )

    val volumes = mapOf(
        "1" to VolumeDetail("1", VolumeInfo("Libro uno", ImageLinks("http://books.google.com/uno"))),
        "2" to VolumeDetail("2", VolumeInfo("Libro dos", ImageLinks("http://books.google.com/dos"))),
        "3" to VolumeDetail("3", VolumeInfo("Libro sin portada", imageLinks = null))
    )

    val expectedBooks = listOf(
        Book("1", "Libro uno", "https://books.google.com/uno"),
        Book("2", "Libro dos", "https://books.google.com/dos")
    )
}