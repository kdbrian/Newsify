package io.github.junrdev.newsify.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDateTime

@Entity(tableName = "news")
@TypeConverters(Converters.NewsSourceConverter::class)
data class NewsItem(
    @PrimaryKey(autoGenerate = true) val id: Long? = 0L,
    val source: NewsSource,
    val author: String? = null,
    val title: String?=null,
    val description: String?=null,
    val url: String?=null,
    val urlToImage: String? = null,
    val publishedAt: String?=null,
    val content: String?=null,
    var isBookMark: Boolean = false,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong() ?: null,
        parcel.readParcelable(NewsSource::class.java.classLoader) ?: NewsSource(null, ""),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readBoolean(),
//        parcel.readSerializable() as LocalDateTime,
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id?:0)
        dest.writeParcelable(source, flags)
        dest.writeString(author)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeString(url)
        dest.writeString(urlToImage)
        dest.writeString(publishedAt)
        dest.writeString(content)
        dest.writeBoolean(isBookMark)
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
