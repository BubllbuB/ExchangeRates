package com.bubllbub.exchangerates.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.di.DaggerAppComponent
import com.bubllbub.exchangerates.di.modules.AppModule
import com.bubllbub.exchangerates.di.modules.RepositoryModule
import com.bubllbub.exchangerates.di.modules.RetrofitModule
import com.bubllbub.exchangerates.di.modules.RoomModule
import com.bubllbub.exchangerates.models.CUR_FAVORITE
import com.bubllbub.exchangerates.models.CUR_QUERY_TRUE
import com.bubllbub.exchangerates.models.Repo
import com.bubllbub.exchangerates.models.UPDATE_DATAS
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Ingot
import com.bubllbub.exchangerates.objects.Rate
import com.bubllbub.exchangerates.views.MainActivity
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

const val CHANNEL_ID = "CURRENCY_NOTIFICATION_CHANNEL_ID"
const val CHANNEL_NAME = "Currency notification"

class UpdateDatabasesWorker(ctx: Context, params: WorkerParameters) : RxWorker(ctx, params) {
    @Inject lateinit var currenciesRepo: Repo<Currency>
    @Inject lateinit var ingotsRepo: Repo<Ingot>
    @Inject lateinit var ratesRepo: Repo<Rate>

    init {
        DaggerAppComponent.builder()
            .appModule(AppModule(App.instance))
            .repositoryModule(RepositoryModule())
            .build()
            .inject(this)
    }

    override fun createWork(): Single<Result> {
        val favoriteCurrencies = mutableListOf<Currency>()

        return ingotsRepo.query()
            .where(UPDATE_DATAS, UPDATE_DATAS)
            .findAll()
            .flatMap {
                ratesRepo.query()
                    .where(UPDATE_DATAS, UPDATE_DATAS)
                    .findAll()
            }
            .flatMap {
                currenciesRepo.query()
                    .where(CUR_FAVORITE, CUR_QUERY_TRUE)
                    .findAll()
            }
            .flatMap {
                favoriteCurrencies.addAll(it)
                currenciesRepo.query()
                    .where(UPDATE_DATAS, UPDATE_DATAS)
                    .findAll()
            }
            .flatMap {
                checkChangesForNotifications(favoriteCurrencies, it)
                Flowable.just(it)
            }
            .firstOrError()
            .map {
                Result.success()
            }
            .onErrorReturn {
                Result.failure()
            }
    }

    private fun checkChangesForNotifications(
        oldCurrList: List<Currency>,
        newCurrList: List<Currency>
    ) {
        oldCurrList.forEach { favCurr ->
            newCurrList.find { it.curId == favCurr.curId }?.let { newCurr ->
                if (favCurr.curOfficialRate == newCurr.curOfficialRate) {
                    notifyCurrency(newCurr)
                }
            }
        }
    }

    private fun notifyCurrency(curr: Currency) {
        createNotifyChannel()

        val mNotificationId: Int = curr.curId
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifyIntent = Intent(applicationContext, MainActivity::class.java)

        val title = "Currency rate it's update"
        val message = "Tap to show chart"

        notifyIntent.putExtra("title", title)
        notifyIntent.putExtra("message", message)
        notifyIntent.putExtra("notification", true)

        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK


        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.er_backdrop_menu)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setContentTitle(title)
            .setContentText(message)
            .build()

        notificationManager.notify(mNotificationId, notification)
    }

    private fun createNotifyChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val context = applicationContext
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.description =
                applicationContext.resources.getString(R.string.notification_channel_description)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}