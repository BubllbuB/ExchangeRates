package com.bubllbub.exchangerates

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.bubllbub.exchangerates.workers.IngotWorker

class App : Application() {
    private lateinit var workManager: WorkManager

    companion object {

        lateinit var instance: App

        fun appContext(): Context = instance.applicationContext

        fun isNetworkAvailable(): Boolean {
            val cm = instance.applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo?.isConnected ?: false
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        workManager = WorkManager.getInstance(applicationContext)
        val ingotWorker = OneTimeWorkRequest.Builder(IngotWorker::class.java).build()
        workManager.enqueue(ingotWorker)
    }
}