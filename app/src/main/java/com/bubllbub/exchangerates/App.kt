package com.bubllbub.exchangerates

import android.content.Context
import android.net.ConnectivityManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bubllbub.exchangerates.di.DaggerAppComponent
import com.bubllbub.exchangerates.di.modules.AdaptersModule
import com.bubllbub.exchangerates.di.modules.AppModule
import com.bubllbub.exchangerates.di.modules.RepositoryModule
import com.bubllbub.exchangerates.workers.UpdateDatabasesWorker
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.DispatchingAndroidInjector
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject


const val DAY_REPEAT_INTERVAL: Long = 1
const val START_HOUR = 12
const val UPDATE_WORKER_TAG = "updateWorker"

class App : DaggerApplication() {
    private lateinit var workManager: WorkManager

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

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
        runWorker()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().appModule(AppModule(this))
            .repositoryModule(RepositoryModule())
            .adaptersModule(AdaptersModule())
            .build()
    }

    private fun runWorker() {
        val delay = if (DateTime.now().withZone(DateTimeZone.forID("Etc/GMT-3")).hourOfDay < START_HOUR) {
            Duration(
                DateTime.now().withZone(DateTimeZone.forID("Etc/GMT-3")),
                DateTime.now().withZone(DateTimeZone.forID("Etc/GMT-3")).withTimeAtStartOfDay().plusHours(
                    START_HOUR
                )
            ).standardMinutes
        } else {
            Duration(
                DateTime.now().withZone(DateTimeZone.forID("Etc/GMT-3")),
                DateTime.now().withZone(DateTimeZone.forID("Etc/GMT-3")).withTimeAtStartOfDay().plusDays(
                    1
                ).plusHours(START_HOUR)
            ).standardMinutes
        }
        workManager = WorkManager.getInstance(applicationContext)

        val updateWorker = PeriodicWorkRequest.Builder(
            UpdateDatabasesWorker::class.java,
            DAY_REPEAT_INTERVAL,
            TimeUnit.DAYS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .addTag(UPDATE_WORKER_TAG)
            .build()


        workManager.enqueueUniquePeriodicWork(
            UPDATE_WORKER_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            updateWorker
        )
    }
}