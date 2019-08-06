package com.bubllbub.exchangerates.workers

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters

private val TAG = IngotWorker::class.java.name

class IngotWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        return try {
            val handler = Handler(Looper.getMainLooper()) {
                Toast.makeText(applicationContext, "Worker do it", Toast.LENGTH_SHORT).show()
                true
            }
            handler.obtainMessage().sendToTarget()
            Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error applying blur", throwable)
            Result.failure()
        }
    }
}