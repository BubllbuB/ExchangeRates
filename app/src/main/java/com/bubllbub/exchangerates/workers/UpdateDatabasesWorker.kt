package com.bubllbub.exchangerates.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.di.DaggerAppComponent
import com.bubllbub.exchangerates.di.modules.AppModule
import com.bubllbub.exchangerates.di.modules.RepositoryModule
import com.bubllbub.exchangerates.extensions.titleForNotification
import com.bubllbub.exchangerates.models.*
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Ingot
import com.bubllbub.exchangerates.objects.Rate
import com.bubllbub.exchangerates.views.MainActivity
import io.reactivex.Single
import javax.inject.Inject

const val CHANNEL_ID = "CURRENCY_NOTIFICATION_CHANNEL_ID"
const val CHANNEL_NAME = "Currency notification"
const val NOTIFICATION_CURRENCY = "currAbbreviation"

class UpdateDatabasesWorker(ctx: Context, params: WorkerParameters) : RxWorker(ctx, params) {
    @Inject
    lateinit var currenciesRepo: Repo<Currency>
    @Inject
    lateinit var ingotsRepo: Repo<Ingot>
    @Inject
    lateinit var ratesRepo: Repo<Rate>

    init {
        DaggerAppComponent.builder()
            .appModule(AppModule(App.instance))
            .repositoryModule(RepositoryModule())
            .build()
            .inject(this)
    }

    override fun createWork(): Single<Result> {
        val favoriteCurrencies = mutableListOf<Currency>()
        val converterCurrencies = mutableListOf<Currency>()

        return ingotsRepo.query()
            .where(UPDATE_DATAS, UPDATE_DATAS)
            .findAll()
            .firstOrError()
            .flatMap {
                ingotsRepo.saveAll(it)
                ratesRepo.query()
                    .where(UPDATE_DATAS, UPDATE_DATAS)
                    .findAll()
                    .firstOrError()
            }
            .flatMap {
                ratesRepo.saveAll(it)
                currenciesRepo.query()
                    .where(CUR_FAVORITE, CUR_QUERY_TRUE)
                    .findAll()
                    .firstOrError()
            }
            .flatMap {
                favoriteCurrencies.addAll(it)
                currenciesRepo.query()
                    .where(CUR_CONVERTER, CUR_QUERY_TRUE)
                    .findAll()
                    .firstOrError()
            }
            .flatMap {
                converterCurrencies.addAll(it)
                currenciesRepo.query()
                    .where(UPDATE_DATAS, UPDATE_DATAS)
                    .findAll()
                    .firstOrError()
            }
            .flatMap { list ->
                favoriteCurrencies.forEach { favCur ->
                    list.find { curr -> favCur.curId == curr.curId }?.let {
                        it.isFavorite = true
                        it.favoritePos = favCur.favoritePos
                    }
                }

                converterCurrencies.forEach { convCur ->
                    list.find { curr -> convCur.curId == curr.curId }?.let {
                        it.isConverter = true
                        it.converterPos = convCur.converterPos
                    }
                }
                currenciesRepo.saveAll(list)
                checkChangesForNotifications(favoriteCurrencies, list)
                Single.just(list)
            }
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
                if (favCurr.curOfficialRate > newCurr.curOfficialRate) {
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

        val title = curr.titleForNotification()
        val message = App.appContext().resources.getString(R.string.notificationSubTitle)

        notifyIntent.putExtra(NOTIFICATION_CURRENCY, curr.curAbbreviation)

        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK


        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            mNotificationId,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val contentViewBig =
            RemoteViews(applicationContext.packageName, R.layout.er_notification)

        contentViewBig.setTextViewText(R.id.titleNotification, title)
        contentViewBig.setTextViewText(R.id.subTitleNotification, message)
        contentViewBig.setImageViewResource(R.id.image_notification, R.drawable.ic_notification)

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
            .setCustomContentView(contentViewBig)
            .setCustomBigContentView(contentViewBig)
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
            notificationChannel.lightColor = Color.parseColor("#7966fe")
            notificationChannel.description =
                applicationContext.resources.getString(R.string.notification_channel_description)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}