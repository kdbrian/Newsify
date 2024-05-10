package io.github.junrdev.newsify.data.remote

import android.content.Context
import android.util.Log
import io.github.junrdev.newsify.model.NewsItem
import io.github.junrdev.newsify.model.NewsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "NewsRepository"

class NewsRepository(val context: Context) {

    private val client = OkHttpClient.Builder()
        .addInterceptor(NewsServiceInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://newsapi.org/v2/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var newsAPIService = retrofit.create(NewsAPIService::class.java)

    val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getNewsBySearchWord(query: String): NewsResponse {

        val news = scope.async {
            newsAPIService.getNewsBySearchString(query)
        }.await()

        return news
    }

    suspend fun getTopHeadlines(): NewsResponse {
        val news = scope.async {
            newsAPIService.getTopHeadlines()
        }.await()

        return news
    }
}