package io.github.junrdev.newsify.util

import android.content.Context
import android.util.Log
import java.io.InputStream
import java.util.Properties

private const val TAG = "ApiKeyProvider"
class ApiKeyProvider(val context: Context) {

    fun getApiKey(): String {
        val properties = Properties()
        val inputStream: InputStream = context.assets.open("local.properties")
        properties.load(inputStream)
        return properties.getProperty("API_KEY")
    }

    companion object {
        fun getApiKey(): String {
            val key =  getApiKey()
            Log.d(TAG, "getApiKey: $key")
            return key
        }
    }
}