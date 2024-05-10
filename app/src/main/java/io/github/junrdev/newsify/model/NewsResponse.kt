package io.github.junrdev.newsify.model

data class NewsResponse (
    val status : String,
    val totalResults : Int,
    val articles : List<NewsItem>
)