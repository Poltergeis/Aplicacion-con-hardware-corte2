package com.softcode.mymagicapp.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.softcode.mymagicapp.cardsfeature.domain.usecases.GetCardsUseCase
import com.softcode.mymagicapp.core.data.session.SessionManager
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.exchangefeature.domain.usecases.GetExchangesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getCardsUseCase: GetCardsUseCase,
    private val getExchangesUseCase: GetExchangesUseCase,
    private val sessionManager: SessionManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Skip sync if there is no active session
        val session = sessionManager.sessionData.firstOrNull() ?: return Result.success()

        val cardsResult = getCardsUseCase()
        val exchangesResult = getExchangesUseCase()

        val failed = listOf(cardsResult, exchangesResult).any { it is OperationResult.Error }
        return if (failed) Result.retry() else Result.success()
    }

    companion object {
        const val WORK_NAME = "periodic_sync"
    }
}
