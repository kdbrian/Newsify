package io.github.junrdev.newsify.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Converters {

    class NewsSourceConverter {
        @TypeConverter
        fun fromSource(source: NewsSource): String {
            return Gson().toJson(source)
        }

        @TypeConverter
        fun toSource(string: String): NewsSource {
            return Gson().fromJson(string, NewsSource::class.java)
        }

    }

    class DateTimeConverter {
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        @TypeConverter
        fun fromTimestamp(value: String?): LocalDateTime {
            return value?.let {
                return formatter.parse(value, LocalDateTime::from)
            }!!
        }

        @TypeConverter
        fun dateToTimestamp(dateTime: LocalDateTime): String {
            return dateTime.format(formatter)
        }

    }

}