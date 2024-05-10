package io.github.junrdev.newsify

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.junrdev.newsify.adapter.BookmarkListAdapter
import io.github.junrdev.newsify.adapter.NewsListAdapter
import io.github.junrdev.newsify.adapter.TopHeadlineAdapter
import io.github.junrdev.newsify.data.local.BookMarkRepository
import io.github.junrdev.newsify.data.remote.NewsRepository
import io.github.junrdev.newsify.model.NewsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), OnQueryTextListener {

    lateinit var capabilities: NetworkCapabilities
    lateinit var newsRepository: NewsRepository
    lateinit var newsList: RecyclerView
    lateinit var topHeadlinesList: RecyclerView
    lateinit var query: SearchView
    lateinit var bookMarkRepository: BookMarkRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        newsList = findViewById(R.id.newsList)
        topHeadlinesList = findViewById(R.id.topHeadlinesList)
        query = findViewById(R.id.query)
        newsRepository = NewsRepository(applicationContext)
        bookMarkRepository = BookMarkRepository(applicationContext)

        newsList.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        topHeadlinesList.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val deleteBookmark = { newsItem: NewsItem ->
            CoroutineScope(Dispatchers.IO).launch {
                bookMarkRepository.deleteBookMark(newsItem)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork

            capabilities = connectivityManager.getNetworkCapabilities(network)!!

            if (checkInternetConnection()) {

                runOnUiThread {
                    //online show online content
                    Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT).show()

                    CoroutineScope(Dispatchers.IO).launch {
                        val topHeadlines = newsRepository.getTopHeadlines()

                        Log.d(TAG, "onCreate: $topHeadlines")

                        withContext(Dispatchers.Main) {
                            topHeadlinesList.adapter =
                                TopHeadlineAdapter(applicationContext, topHeadlines.articles) {
                                    onNewsSelected(it)
                                }
                        }
                    }
                    query.setOnQueryTextListener(this)
                }


            } else {
                //offline show bookmarks
                Toast.makeText(applicationContext, "Not connected", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val bookmarks = bookMarkRepository.getBookMarks()
                    newsList.adapter = BookmarkListAdapter(
                        applicationContext,
                        bookmarks,
                        onNewsSelected = { onNewsSelected(it) },
                        onNewsDeleted = { deleteBookmark(it) })
                }

            }
        }

    }


    private fun checkInternetConnection(): Boolean {
        return (capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )
                && (
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                )
                )
    }

    private fun onNewsSelected(newsItem: NewsItem) {
        val intent = Intent(this, Actions::class.java)
        intent.putExtra("selectedNews", newsItem)
        startActivity(intent)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (checkInternetConnection()) {
            newsList.adapter = null

            if (query?.isNotEmpty()!!) {

                val searchQuery = when (query?.contains(" ")) {
                    true -> query.replace(" ", "%20")
                    else -> query
                }

                Log.d(TAG, "onQueryTextSubmit: search -> $searchQuery")

                CoroutineScope(Dispatchers.IO).launch {
                    val news = newsRepository.getNewsBySearchWord(searchQuery)

                    withContext(Dispatchers.Main) {
                        val adapter =
                            NewsListAdapter(applicationContext, news.articles, bookMarkRepository) {
                                onNewsSelected(it)
                            }
                        newsList.adapter = adapter
                    }

                }

            } else
                Toast.makeText(
                    applicationContext,
                    "Get back online to continue.",
                    Toast.LENGTH_SHORT
                )
                    .show()
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        /*newsList.adapter = null
        if (newText?.isNotEmpty()!!)
            CoroutineScope(Dispatchers.IO).launch {
                val news = newsRepository.getNewsBySearchWord(newText!!)

                withContext(Dispatchers.Main) {
                    val adapter = NewsListAdapter(applicationContext, news.articles){
                        onNewsSelected(it)
                    }
                    newsList.adapter = adapter
                }

            }

         */
        return true
    }
}