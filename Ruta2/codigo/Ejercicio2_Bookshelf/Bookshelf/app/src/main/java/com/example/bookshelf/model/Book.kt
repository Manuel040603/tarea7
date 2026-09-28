package com.example.bookshelf.model

import kotlinx.serialization.Serializable

@Serializable
data class BooksSearchResponse(
    val items: List<VolumeSummary> = emptyList()
)

@Serializable
data class VolumeSummary(
    val id: String,
    val volumeInfo: VolumeInfo = VolumeInfo()
)

@Serializable
data class VolumeDetail(
    val id: String,
    val volumeInfo: VolumeInfo = VolumeInfo()
)

@Serializable
data class VolumeInfo(
    val title: String = "",
    val imageLinks: ImageLinks? = null
)

@Serializable
data class ImageLinks(
    val thumbnail: String = ""
)

data class Book(
    val id: String,
    val title: String,
    val thumbnailUrl: String
)

fun VolumeDetail.toBook(): Book = volumeInfo.toBook(id)

fun VolumeSummary.toBook(): Book = volumeInfo.toBook(id)

private fun VolumeInfo.toBook(id: String): Book = Book(
    id = id,
    title = title,
    thumbnailUrl = imageLinks?.thumbnail.orEmpty().replace("http://", "https://")
)