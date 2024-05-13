package io.github.junrdev.newsify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.junrdev.newsify.model.NewsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = arrayOf(NewsItem::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookMarkDao(): BookmarkDao
    abstract fun cacheDao(): NewsCacheDao

    companion object {

        @Volatile
        var instance: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase {
            val CURR = instance ?: synchronized(this) {
                val appDatabase =
                    Room.databaseBuilder(context, AppDatabase::class.java, "newsbooksmarkdb")
                        .build()
                instance = appDatabase
                appDatabase
            }


            return CURR
        }
    }
}