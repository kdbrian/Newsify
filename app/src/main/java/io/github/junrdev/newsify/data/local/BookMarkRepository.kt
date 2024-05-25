package io.github.junrdev.newsify.data.local

import android.content.Context
import android.widget.Toast
import io.github.junrdev.newsify.model.NewsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookMarkRepository(
    val context: Context
) {

    val scope = CoroutineScope(Dispatchers.IO)
    val bookmarkDao = AppDatabase.getAppDatabase(context).bookMarkDao()

    fun addToBookMark(newsItem: NewsItem, onComplete: (() -> Unit)? = null) {
        scope.launch {
            newsItem.isBookMark = true
            bookmarkDao.insertBookMark(newsItem)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Added bookmark", Toast.LENGTH_SHORT).show()
            }
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