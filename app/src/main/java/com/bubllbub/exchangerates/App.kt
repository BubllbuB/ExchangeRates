package com.bubllbub.exchangerates

import android.content.Context
import android.net.ConnectivityManager
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bubllbub.exchangerates.di.AppComponent
import com.bubllbub.exchangerates.di.DaggerAppComponent
import com.bubllbub.exchangerates.di.modules.AdaptersModule
import com.bubllbub.exchangerates.di.modules.AppModule
import com.bubllbub.exchangerates.di.modules.RepositoryModule
import com.bubllbub.exchangerates.workers.UpdateDatabasesWorker
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.DispatchingAndroidInjector
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject


const val DAY_REPEAT_INTERVAL: Long = 1
const val START_HOUR = 19
const val UPDATE_WORKER_TAG = "updateWorker"

class App : DaggerApplication() {
    private lateinit var workManager: WorkManager

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    companion object {
        lateinit var applicationComponent: AppComponent
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
        //runWorker()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().appModule(AppModule(this))
            .repositoryModule(RepositoryModule())
            .adaptersModule(AdaptersModule())
            .build()
    }

    private fun runWorker() {
        val delay = if (DateTime.now().hourOfDay < START_HOUR) {
            Duration(
                DateTime.now(),
                DateTime.now().withTimeAtStartOfDay().plusHours(START_HOUR)
            ).standardMinutes
        } else {
            Duration(
                DateTime.now(),
                DateTime.now().withTimeAtStartOfDay().plusDays(1).plusHours(START_HOUR)
            ).standardMinutes
        }
        workManager = WorkManager.getInstance(applicationContext)

        val updateWorkerOneTime =
            OneTimeWorkRequest.Builder(UpdateDatabasesWorker::class.java).build()
        workManager.enqueue(updateWorkerOneTime)

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


        /*workManager.enqueueUniquePeriodicWork(
            UPDATE_WORKER_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            updateWorker
        )*/
    }
}