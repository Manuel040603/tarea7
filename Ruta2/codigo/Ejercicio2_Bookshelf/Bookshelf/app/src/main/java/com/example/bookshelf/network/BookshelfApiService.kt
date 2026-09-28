package com.example.bookshelf.network

import com.example.bookshelf.model.BooksSearchResponse
import com.example.bookshelf.model.VolumeDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookshelfApiService {

    @GET("volumes")
    suspend fun searchBooks(@Query("q") query: String): BooksSearchResponse

    @GET("volumes/{id}")
    suspend fun getBook(@Path("id") id: String): VolumeDetail
}
