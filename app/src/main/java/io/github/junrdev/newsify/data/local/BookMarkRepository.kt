package io.github.junrdev.newsify.data.local

import android.content.Context
import io.github.junrdev.newsify.model.NewsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BookMarkRepository(
    val context: Context
) {

    val scope = CoroutineScope(Dispatchers.IO)
    val bookmarkDao = AppDatabase.getAppDatabase(context).bookMarkDao()

    fun addToBookMark(newsItem: NewsItem) {
        scope.launch {
            bookmarkDao.insertBookMark(newsItem)
        }
    }

    suspend fun getBookMarks(): List<NewsItem> {
        return scope.async {
            bookmarkDao.getBookMarks()
        }.await()
    }

    suspend fun deleteBookMark(newsItem: NewsItem) {
        scope.launch {
            bookmarkDao.deleteBookMark(newsItem)
        }
    }
}