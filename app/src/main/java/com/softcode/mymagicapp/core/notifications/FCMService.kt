package com.softcode.mymagicapp.core.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.softcode.mymagicapp.core.data.session.SessionManager
import com.softcode.mymagicapp.core.network.CardsApi
import com.softcode.mymagicapp.core.network.UpdateFcmTokenRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject lateinit var api: CardsApi
    @Inject lateinit var sessionManager: SessionManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Called when FCM issues a new registration token.
     * Send it to the backend so the server can target this device.
     */
    override fun onNewToken(token: String) {
        serviceScope.launch {
            val session = sessionManager.sessionData.firstOrNull() ?: return@launch
            runCatching { api.updateFcmToken(UpdateFcmTokenRequest(token)) }
        }
    }

    /**
     * Called when a push notification arrives while the app is in foreground.
     * Route to the correct channel based on the notification type.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: return
        val body = message.notification?.body ?: return

        // Convention: backend sends `type` in data payload to pick the channel
        val channelId = when (message.data["type"]) {
            "exchange_response" -> NotificationHelper.CHANNEL_RESPONSES
            else -> NotificationHelper.CHANNEL_PROPOSALS
        }

        NotificationHelper.show(applicationContext, title, body, channelId)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.coroutineContext[kotlinx.coroutines.Job]?.cancel()
    }
}
