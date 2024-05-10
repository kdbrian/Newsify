package io.github.junrdev.newsify

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.junrdev.newsify.data.local.BookmarkDao
import io.github.junrdev.newsify.fragments.BookMarks
import io.github.junrdev.newsify.fragments.ViewNewsItem
import io.github.junrdev.newsify.model.NewsItem
import io.github.junrdev.newsify.model.NewsSource
import java.io.Serializable

private const val TAG = "Actions"

class Actions : AppCompatActivity() {

    lateinit var fragmnentHost: FrameLayout
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var bookmarkDao: BookmarkDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actions)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fragmnentHost = findViewById(R.id.fragmentHost)
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        val selectedNews = intent.getParcelableExtra<NewsItem>("selectedNews")

        Log.d(TAG, "onCreate: you selected -> $selectedNews")

        moveToFragment(ViewNewsItem(applicationContext, selectedNews!!))

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.newsDetails -> {
                    moveToFragment(ViewNewsItem(applicationContext, selectedNews))
                }

                R.id.bookmarks -> {
                    moveToFragment(BookMarks(applicationContext, ))
                }

            }
            true
        }
    }


    private fun moveToFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentHost, fragment)
        transaction.commit()
    }
}