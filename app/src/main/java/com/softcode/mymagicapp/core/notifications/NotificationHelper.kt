package com.softcode.mymagicapp.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.softcode.mymagicapp.R

object NotificationHelper {

    const val CHANNEL_PROPOSALS = "exchange_proposals"
    const val CHANNEL_RESPONSES = "exchange_responses"

    /** Call once at app startup (idempotent). */
    fun createChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_PROPOSALS,
                "Propuestas de intercambio",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando recibes una nueva propuesta de intercambio"
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_RESPONSES,
                "Respuestas de intercambio",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones cuando tu propuesta es aceptada o rechazada"
            }
        )
    }

    fun show(context: Context, title: String, body: String, channelId: String = CHANNEL_PROPOSALS) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
