package io.github.junrdev.newsify.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import io.github.junrdev.newsify.R
import io.github.junrdev.newsify.data.local.BookMarkRepository
import io.github.junrdev.newsify.model.NewsItem

private const val TAG = "ViewNewsItem"

class ViewNewsItem(
    val parentContext: Context,
    val newsItem: NewsItem
) : Fragment() {

    lateinit var newsPreview: ImageView
    lateinit var newsTitle: TextView
    lateinit var newsDescription: TextView
    lateinit var newsAuthor: TextView
    lateinit var readMore: CardView
    lateinit var bookmarksNews: ImageView
    lateinit var bookMarkRepository: BookMarkRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_news_item, container, false)

        bookMarkRepository = BookMarkRepository(parentContext?.applicationContext!!)

        view.apply {
            newsPreview = findViewById(R.id.newsPreview)
            newsTitle = findViewById(R.id.newsTitle)
            newsDescription = findViewById(R.id.newsDescription)
            newsAuthor = findViewById(R.id.newsAuthor)
            readMore = findViewById(R.id.readMore)
            bookmarksNews = findViewById(R.id.bookmarksNews)
        }

        newsItem.apply {


            urlToImage?.let {
                Glide.with(context?.applicationContext!!)
                    .load(urlToImage)
                    .centerCrop()
                    .placeholder(R.drawable.demoimg)
                    .into(newsPreview)
            }

            newsTitle.setText(title)
            newsDescription.setText(description)
            newsAuthor.setText(author)

            url?.let { link ->
                Log.d(TAG, "onCreateView: link -> $link")
                readMore.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)

                    intent.data = Uri.parse(link)

//                    if (intent.resolveActivity(parentContext.packageManager) != null) {
                        startActivity(intent)
//                    }
                }
            }

            bookmarksNews.setOnClickListener {
                bookMarkRepository.addToBookMark(newsItem)
            }
        }

        return view
    }

}