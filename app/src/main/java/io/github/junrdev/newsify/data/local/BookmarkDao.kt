package io.github.junrdev.newsify.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.github.junrdev.newsify.model.NewsItem

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookMark(newsItem : NewsItem)

    @Query("SELECT * FROM news WHERE isBookMark=1")
    suspend fun getBookMarks() : List<NewsItem>

    @Delete
    suspend fun deleteBookMark(newsItem: NewsItem)

}