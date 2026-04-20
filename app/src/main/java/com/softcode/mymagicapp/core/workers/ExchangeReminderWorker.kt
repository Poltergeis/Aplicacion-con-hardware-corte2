package com.softcode.mymagicapp.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.softcode.mymagicapp.core.data.local.dao.ExchangeDao
import com.softcode.mymagicapp.core.data.local.dao.UserDao
import com.softcode.mymagicapp.core.data.session.SessionManager
import com.softcode.mymagicapp.core.notifications.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

@HiltWorker
class ExchangeReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val sessionManager: SessionManager,
    private val userDao: UserDao,
    private val exchangeDao: ExchangeDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val session = sessionManager.sessionData.firstOrNull() ?: return Result.success()
        val user = userDao.getUserByName(session.username) ?: return Result.success()

        val thresholdTimestamp = System.currentTimeMillis() - REMINDER_THRESHOLD_MS
        val pending = exchangeDao.getPendingReceivedExchanges(user.id, thresholdTimestamp)

        if (pending.isNotEmpty()) {
            val message = if (pending.size == 1) {
                "${pending.first().proposerUsername} te propuso un intercambio y sigue esperando tu respuesta"
            } else {
                "Tienes ${pending.size} propuestas de intercambio esperando tu respuesta"
            }
            NotificationHelper.show(
                applicationContext,
                "Intercambio pendiente",
                message,
                NotificationHelper.CHANNEL_PROPOSALS
            )
        }

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "exchange_reminder"
        private val REMINDER_THRESHOLD_MS = TimeUnit.HOURS.toMillis(6)
    }
}
