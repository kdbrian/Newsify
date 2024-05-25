package io.github.junrdev.newsify.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.junrdev.newsify.R
import io.github.junrdev.newsify.adapter.BookmarkListAdapter
import io.github.junrdev.newsify.data.local.BookMarkRepository
import io.github.junrdev.newsify.model.NewsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BookMarks(val parentContext: Context) : Fragment() {

    lateinit var bookmarksList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_book_marks, container, false)

        val bookMarkRepository = BookMarkRepository(parentContext?.applicationContext!!)

        bookmarksList = view.findViewById(R.id.bookmarksList)

        val onNewsDeleted = fun(newsItem: NewsItem) {
            CoroutineScope(Dispatchers.IO).launch {
                bookMarkRepository.deleteBookMark(newsItem)
            }
        }

        val onNewsSelected = fun(newsItem: NewsItem) {
            val fragmentManager = fragmentManager
            val transaction = fragmentManager?.beginTransaction()!!
            transaction.replace(R.id.fragmentHost, ViewNewsItem(parentContext, newsItem))
            transaction.commit()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val bookMarks = bookMarkRepository.getBookMarks()

            Log.d(TAG, "onCreateView: ${bookMarks.size}")
            Log.d(TAG, "onCreateView: $bookMarks")

            withContext(Dispatchers.Main) {
                bookmarksList.adapter =
                    BookmarkListAdapter(
                        parentContext?.applicationContext!!,
                        bookMarks,
                        onNewsSelected,
                        onNewsDeleted
                    )

            }
        }

        return view
    }


    companion object{
        private const val TAG = "BookMarks"
    }
}