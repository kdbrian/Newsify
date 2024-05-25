package io.github.junrdev.newsify

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import io.github.junrdev.newsify.adapter.BookmarkListAdapter
import io.github.junrdev.newsify.adapter.NewsListAdapter
import io.github.junrdev.newsify.adapter.TopHeadlineAdapter
import io.github.junrdev.newsify.data.local.AppDatabase
import io.github.junrdev.newsify.data.local.BookMarkRepository
import io.github.junrdev.newsify.data.local.CacheRepository
import io.github.junrdev.newsify.data.remote.NewsRepository
import io.github.junrdev.newsify.model.NewsItem
import io.github.junrdev.newsify.util.ConnectivityReciever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
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
    lateinit var cacheRepository: CacheRepository
    lateinit var noInternet: TextView
    lateinit var connectivityReciever: ConnectivityReciever

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
        noInternet = findViewById(R.id.noInternet)
        newsRepository = NewsRepository(applicationContext)
        bookMarkRepository = BookMarkRepository(applicationContext)
        connectivityReciever = ConnectivityReciever()
        cacheRepository = CacheRepository(
            applicationContext,
            AppDatabase.getAppDatabase(applicationContext).cacheDao()
        )
        Glide.with(applicationContext)
            .setDefaultRequestOptions(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            )

        newsList.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        topHeadlinesList.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        registerReceiver(connectivityReciever, filter)
        updateCache()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork
            if (network != null) {

                capabilities = connectivityManager.getNetworkCapabilities(network)!!

                if (checkInternetConnection()) {

                    //online show online content
                    Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT).show()

                    CoroutineScope(Dispatchers.IO).launch {
                        val topHeadlines = newsRepository.getTopHeadlines(onError = { onError(it) })

                        topHeadlines?.let {

                            Log.d(TAG, "onCreate: $topHeadlines")

                            withContext(Dispatchers.Main) {
                                topHeadlinesList.adapter =
                                    TopHeadlineAdapter(
                                        applicationContext,
                                        topHeadlines.articles
                                    ) { item, holder ->
                                        onNewsSelected(item, holder)
                                    }
                            }
                        }

                    }
                    query.setOnQueryTextListener(this)

                } else {

                    //online no internet show cache
                    Toast.makeText(applicationContext, "Not connected", Toast.LENGTH_SHORT).show()
                    loadCache()
                }
            } else {
                //offline show cache
                Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
                loadCache()
            }


        }

    }

    private fun loadCache(){

        newsList.adapter = null
        CoroutineScope(Dispatchers.IO).launch {
            val cache = cacheRepository.getCachedNews()

            Log.d(TAG, "onCreate: cache $cache")

            if (cache.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "onCreate: cache $cache")

                    newsList.adapter = NewsListAdapter(
                        applicationContext,
                        cache,
                        bookMarkRepository,
                        onNoteSelected = { newsItem -> onNewsSelected(newsItem) }
                    )
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

    private fun onNewsSelected(newsItem: NewsItem, holder: TopHeadlineAdapter.VH? = null) {
        val intent = Intent(this, Actions::class.java)
        intent.putExtra("selectedNews", newsItem)
        startActivity(intent)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        newsList.adapter = null
        if (checkInternetConnection()) {
            if (query?.isNotEmpty()!!) {

                val searchQuery = when (query?.contains(" ")) {
                    true -> query.replace(" ", "%20")
                    else -> query
                }

                Log.d(TAG, "onQueryTextSubmit: search -> $searchQuery")

                CoroutineScope(Dispatchers.IO).launch {
                    val news =
                        newsRepository.getNewsBySearchWord(searchQuery, onError = { onError(it) })!!

                    // add to cache
                    cacheRepository.deleteCachePast()

                    if (news.articles.isNotEmpty()) {
                        cacheRepository.addNewsToCache(news.articles)
                    }

                    withContext(Dispatchers.Main) {
                        val adapter =
                            NewsListAdapter(
                                applicationContext,
                                news.articles,
                                bookMarkRepository
                            ) {
                                onNewsSelected(it)
                            }
                        newsList.adapter = adapter
                    }

                }.invokeOnCompletion {
                    it?.cause?.let { throwable ->
                        Log.d(TAG, "onQueryTextSubmit: ${throwable.localizedMessage}")
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

    fun onError(errorMessage: String) {
        Toast.makeText(applicationContext, "Failed due to $errorMessage", Toast.LENGTH_SHORT).show()
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

    private val onNetworkStateChange = fun(state: Boolean) {
        when (state) {
            true -> {
                noInternet.visibility = View.GONE
            }

            else -> {
                noInternet.visibility = View.VISIBLE
            }
        }
    }

    private val updateCache = {
        CoroutineScope(Dispatchers.IO).launch {
            if (cacheRepository.getCachedNews().isNotEmpty()) {

                withContext(Dispatchers.Main) {
                    Handler().postDelayed(
                        {
                            deleteCache()
                        }, 5 * 60 * 1000
                    )
                }
            }
        }
    }

    private fun deleteCache() {
        CoroutineScope(Dispatchers.IO).launch {
            cacheRepository.deleteCachePast()
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(connectivityReciever, filter)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(connectivityReciever, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectivityReciever)
    }

    companion object {
        private val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

    }
}

