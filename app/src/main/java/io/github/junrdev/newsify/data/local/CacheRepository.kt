package io.github.junrdev.newsify.data.local

import android.content.Context
import io.github.junrdev.newsify.model.Converters
import io.github.junrdev.newsify.model.NewsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CacheRepository(val context: Context, val cacheDao: NewsCacheDao) {

    val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getCachedNews(): List<NewsItem> {
        return scope.async {
            cacheDao.getCachedNews()
        }.await()
    }

    suspend fun addNewsToCache(news: List<NewsItem>) {
        scope.launch {
            cacheDao.insertIntoCache(news)
        }
    }

    suspend fun deleteCachePast(localDateTime: LocalDateTime? = null) {
//        Converters.DateTimeConverter().dateToTimestamp(localDateTime)
        scope.launch {
            cacheDao.clearCache()
        }
    }

}