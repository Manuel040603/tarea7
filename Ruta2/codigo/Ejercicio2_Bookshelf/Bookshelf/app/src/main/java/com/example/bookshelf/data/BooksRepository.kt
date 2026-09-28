package com.example.bookshelf.data

import com.example.bookshelf.model.Book
import com.example.bookshelf.model.toBook
import com.example.bookshelf.network.BookshelfApiService
import retrofit2.HttpException

interface BooksRepository {
    suspend fun getBooks(query: String): List<Book>
}

class NetworkBooksRepository(
    private val bookshelfApiService: BookshelfApiService
) : BooksRepository {

    override suspend fun getBooks(query: String): List<Book> {

        val results = bookshelfApiService.searchBooks(query).items

        val books = mutableListOf<Book>()
        for (item in results) {
            val book = try {
                bookshelfApiService.getBook(item.id).toBook()
            } catch (e: HttpException) {
                item.toBook()
            }
            books.add(book)
        }

        return books.filter { it.thumbnailUrl.isNotBlank() }
    }
}