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

class TopHeadlineAdapter(
    val context: Context,
    val topHeadlines: List<NewsItem>,
    val onNewsPicked: ((newsItem: NewsItem) -> Unit)? = null
) :
    RecyclerView.Adapter<TopHeadlineAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val newsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        val preview: ImageView = itemView.findViewById(R.id.newsPreview)
        val newsCard: CardView = itemView.findViewById(R.id.newsCard)

        fun bind(newsItem: NewsItem) {
            newsItem.apply {
                newsTitle.setText(newsItem.title)

                urlToImage?.let {
                    Glide.with(context.applicationContext)
                        .load(it)
                        .centerCrop()
                        .into(preview)
                }
            }

            newsCard.setOnClickListener {
                onNewsPicked?.invoke(newsItem)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.topheadlineitem, parent, false)
        )
    }

    override fun getItemCount(): Int = topHeadlines.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cur = topHeadlines[position]
        holder.bind(cur)
    }
}