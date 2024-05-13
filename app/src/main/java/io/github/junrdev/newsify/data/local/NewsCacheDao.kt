package io.github.junrdev.newsify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.junrdev.newsify.model.NewsItem
import java.time.LocalDateTime

@Dao
interface NewsCacheDao {

    @Insert// (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntoCache(news : List<NewsItem>)

    @Query("DELETE FROM news WHERE isBookMark = 0") // remove all news that are not bookmarked
    suspend fun clearCache()

    @Query("SELECT * FROM news WHERE isBookMark=false")
    suspend fun getCachedNews() : List<NewsItem>

//    suspend fun getItemFromCache()
}