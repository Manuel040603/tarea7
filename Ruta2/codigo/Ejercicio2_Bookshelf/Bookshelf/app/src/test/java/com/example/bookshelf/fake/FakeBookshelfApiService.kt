package com.example.bookshelf.fake

import com.example.bookshelf.model.BooksSearchResponse
import com.example.bookshelf.model.VolumeDetail
import com.example.bookshelf.network.BookshelfApiService

class FakeBookshelfApiService : BookshelfApiService {
    override suspend fun searchBooks(query: String): BooksSearchResponse =
        FakeDataSource.searchResponse

    override suspend fun getBook(id: String): VolumeDetail =
        FakeDataSource.volumes.getValue(id)
}
