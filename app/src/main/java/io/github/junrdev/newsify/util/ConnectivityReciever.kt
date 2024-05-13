package io.github.junrdev.newsify.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log


private const val TAG = "ConnectivityReciever"

class ConnectivityReciever(val onNetworkStateChange: (connected: Boolean) -> Unit) :
    BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION)
            if (checkInternetConnection(context!!)) {
                Log.d(TAG, "onReceive: network true")
                onNetworkStateChange(true)
            } else {
                Log.d(TAG, "onReceive: network false")
                onNetworkStateChange(false)
            }
    }

    private fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val info = connectivityManager.activeNetworkInfo

        return network?.let {
            val capabilities = connectivityManager.getNetworkCapabilities(network)!!
            return (capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    )
                    && (
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                    ))
        }?:false

    }
}