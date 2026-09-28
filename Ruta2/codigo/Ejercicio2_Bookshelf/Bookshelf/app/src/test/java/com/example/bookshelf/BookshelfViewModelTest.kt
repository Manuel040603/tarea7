package com.example.bookshelf

import com.example.bookshelf.fake.FakeBooksRepository
import com.example.bookshelf.fake.FakeDataSource
import com.example.bookshelf.fake.FakeErrorBooksRepository
import com.example.bookshelf.rules.TestDispatcherRule
import com.example.bookshelf.ui.screens.BookshelfUiState
import com.example.bookshelf.ui.screens.BookshelfViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BookshelfViewModelTest {

    @get:Rule
    val testDispatcher = TestDispatcherRule()

    @Test
    fun bookshelfViewModel_getBooks_verifyUiStateSuccess() = runTest {
        val viewModel = BookshelfViewModel(booksRepository = FakeBooksRepository())
        assertEquals(BookshelfUiState.Success(FakeDataSource.expectedBooks), viewModel.bookshelfUiState)
    }

    @Test
    fun bookshelfViewModel_networkError_verifyUiStateError() = runTest {
        val viewModel = BookshelfViewModel(booksRepository = FakeErrorBooksRepository())
        assertEquals(BookshelfUiState.Error, viewModel.bookshelfUiState)
    }
}
