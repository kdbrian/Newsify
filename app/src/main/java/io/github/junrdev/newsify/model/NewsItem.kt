package io.github.junrdev.newsify.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson

@Entity()
@TypeConverters(NewsSourceConverter::class)
data class NewsItem(
    val source: NewsSource,
    val author: String? = null,
    @PrimaryKey val title: String,
    val description: String,
    val url: String,
    val urlToImage: String? = null,
    val publishedAt: String,
    val content: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(NewsSource::class.java.classLoader) ?: NewsSource(null, ""),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(source, flags)
        dest.writeString(author)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeString(url)
        dest.writeString(urlToImage)
        dest.writeString(publishedAt)
        dest.writeString(content)
    }

    companion object CREATOR : Parcelable.Creator<NewsItem> {
        override fun createFromParcel(parcel: Parcel): NewsItem {
            return NewsItem(parcel)
        }

        override fun newArray(size: Int): Array<NewsItem?> {
            return arrayOfNulls(size)
        }
    }
}


data class NewsSource(
    val id: String? = null,
    val name: String
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString() ?: ""
    )

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
    }

    companion object CREATOR : Parcelable.Creator<NewsSource> {
        override fun createFromParcel(parcel: Parcel): NewsSource {
            return NewsSource(parcel)
        }

        override fun newArray(size: Int): Array<NewsSource?> {
            return arrayOfNulls(size)
        }
    }

}


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
