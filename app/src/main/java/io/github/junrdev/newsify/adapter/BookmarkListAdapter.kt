package io.github.junrdev.newsify.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.junrdev.newsify.R
import io.github.junrdev.newsify.model.NewsItem

class BookmarkListAdapter(
    val context: Context,
    val news: List<NewsItem>,
    val onNewsSelected: ((newsItem: NewsItem) -> Unit)? = null,
    val onNewsDeleted: ((newsItem: NewsItem) -> Unit)? = null
) :
    RecyclerView.Adapter<BookmarkListAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val previewImage: ImageView = itemView.findViewById(R.id.newsPreview)
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val deleteBookmark: ImageView = itemView.findViewById(R.id.deleteBookmark)
        val publishedAt: TextView = itemView.findViewById(R.id.publishedAt)
        val newsCard: CardView = itemView.findViewById(R.id.newsCard)

        fun bindNews(
            newsItem: NewsItem,
            position: Int
        ) {

            title.setText(newsItem.title)
            publishedAt.setText(newsItem.publishedAt)
            newsItem.urlToImage?.let {
                Glide.with(context)
                    .load(it)
                    .centerCrop()
                    .placeholder(R.drawable.demoimg)
                    .into(previewImage)
            }

            deleteBookmark.setOnClickListener {
                onNewsDeleted?.invoke(newsItem)
                notifyItemRemoved(position)
            }

            newsCard.setOnClickListener {
                onNewsSelected?.invoke(newsItem)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.bookmarkitem, parent, false))
    }

    override fun getItemCount(): Int = news.size


    override fun onBindViewHolder(holder: VH, position: Int) {
        val newsItem = news[position]
        holder.bindNews(newsItem, position)
    }
}