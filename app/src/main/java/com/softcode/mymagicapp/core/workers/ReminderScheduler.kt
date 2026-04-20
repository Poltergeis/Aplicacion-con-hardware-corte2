package com.softcode.mymagicapp.core.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    /**
     * Schedules a periodic check every 6 hours for unanswered exchange proposals.
     * No network constraint — works entirely from the local Room cache.
     * Uses KEEP policy so re-scheduling after login doesn't reset the timer.
     */
    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<ExchangeReminderWorker>(6, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ExchangeReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    /** Cancels the reminder — call on logout. */
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(ExchangeReminderWorker.WORK_NAME)
    }
}
