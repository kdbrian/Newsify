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

    suspend fun getNewsBySearchWord(
        query: String,
        onError: ((error: String) -> Unit)? = null
    ): NewsResponse? {
        val response = newsAPIService.getNewsBySearchString(query)

        Log.d(TAG, "getNewsBySearchWord: ${response.isSuccessful}")

        if ( ! response.isSuccessful) {
            onError?.invoke(response.errorBody()?.string()!!)
        }

        return response.body() ?: null
    }

    suspend fun getTopHeadlines(onError: ((error: String) -> Unit)? = null): NewsResponse? {
        val response = newsAPIService.getTopHeadlines()

        if (!response.isSuccessful) {
            onError?.invoke(response.errorBody()?.string()!!)
        }

        return response.body() ?: null
    }
}