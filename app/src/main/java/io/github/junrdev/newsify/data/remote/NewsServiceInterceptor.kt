package io.github.junrdev.newsify.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

private const val TAG = "NewsServiceInterceptor"

class NewsServiceInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d(TAG, "intercept: ${chain.request().url()}")
        return chain.proceed(chain.request())
    }
}