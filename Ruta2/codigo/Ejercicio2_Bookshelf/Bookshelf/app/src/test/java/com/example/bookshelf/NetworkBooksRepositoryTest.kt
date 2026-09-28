package com.example.bookshelf

import com.example.bookshelf.data.NetworkBooksRepository
import com.example.bookshelf.fake.FakeBookshelfApiService
import com.example.bookshelf.fake.FakeDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkBooksRepositoryTest {

    private val repository = NetworkBooksRepository(
        bookshelfApiService = FakeBookshelfApiService()
    )

    @Test
    fun networkBooksRepository_getBooks_verifyBookList() = runTest {
        assertEquals(FakeDataSource.expectedBooks, repository.getBooks("jazz history"))
    }

    @Test
    fun networkBooksRepository_getBooks_thumbnailsUseHttps() = runTest {
        val books = repository.getBooks("jazz history")
        assertTrue(books.all { it.thumbnailUrl.startsWith("https://") })
    }
}
