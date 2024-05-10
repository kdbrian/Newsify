package io.github.junrdev.newsify.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.junrdev.newsify.model.NewsItem

@Dao
interface BookmarkDao {

    @Insert
    suspend fun insertBookMark(newsItem : NewsItem)

    @Query("SELECT * FROM newsitem")
    suspend fun getBookMarks() : List<NewsItem>

    @Delete
    suspend fun deleteBookMark(newsItem: NewsItem)

}