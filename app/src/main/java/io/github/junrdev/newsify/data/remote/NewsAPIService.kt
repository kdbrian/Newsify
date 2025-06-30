package io.github.junrdev.newsify.data.remote

import io.github.junrdev.newsify.model.NewsItem
import io.github.junrdev.newsify.model.NewsResponse
import io.github.junrdev.newsify.util.ApiKeyProvider
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import io.github.junrdev.newsify.BuildConfig



interface NewsAPIService {

    @GET("everything")
    suspend fun getNewsBySearchString(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Response<NewsResponse>

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "US".lowercase(),
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Response<NewsResponse>

}
