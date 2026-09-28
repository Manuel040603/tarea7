package com.example.bookshelf.fake

import com.example.bookshelf.data.BooksRepository
import com.example.bookshelf.model.Book
import java.io.IOException

class FakeBooksRepository : BooksRepository {
    override suspend fun getBooks(query: String): List<Book> = FakeDataSource.expectedBooks
}

class FakeErrorBooksRepository : BooksRepository {
    override suspend fun getBooks(query: String): List<Book> =
        throw IOException("Sin conexión (simulado)")
}
