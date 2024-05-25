package io.github.junrdev.newsify.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.github.junrdev.newsify.R
import io.github.junrdev.newsify.data.local.BookMarkRepository
import io.github.junrdev.newsify.model.NewsItem

class NewsListAdapter(
    val context: Context,
    val news: List<NewsItem>,
    val bookMarkRepository: BookMarkRepository,
    val onNoteSelected: ((newsItem: NewsItem) -> Unit)? = null
) :
    RecyclerView.Adapter<NewsListAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val previewImage: ImageView = itemView.findViewById(R.id.newsPreview)
        val sheet: LinearLayout = itemView.findViewById(R.id.sheet)
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val bookmarkNews: ImageView = itemView.findViewById(R.id.bookmarkNews)
        val publishedAt: TextView = itemView.findViewById(R.id.publishedAt)
        val newsCard: CardView = itemView.findViewById(R.id.newsCard)

        fun bindNews(
            newsItem: NewsItem,
            onBookmark: (() -> Unit)? = null
        ) {

            title.setText(newsItem.title)
            publishedAt.setText(newsItem.publishedAt)
            newsItem.urlToImage?.let {
                Glide.with(context)
                    .asBitmap()
                    .load(it)
                    .centerCrop()
//                    .apply(RequestOptions().over)
                    .placeholder(R.drawable.demoimg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(
                        object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                Palette.from(resource).generate { palette ->
                                    val dominantColor = palette?.dominantSwatch?.rgb ?: Color.BLACK
                                    sheet.setBackgroundColor(dominantColor)
                                    previewImage.setImageBitmap(resource)
                                }

                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        }
                    )

            }

            bookmarkNews.setOnClickListener {
                onBookmark?.invoke()
            }

            newsCard.setOnClickListener {
                onNoteSelected?.invoke(newsItem)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.newsitem, parent, false))
    }

    override fun getItemCount(): Int = news.size


    override fun onBindViewHolder(holder: VH, position: Int) {
        val newsItem = news[position]
        holder.bindNews(newsItem, onBookmark = {
            bookMarkRepository.addToBookMark(newsItem)
        })
    }
}