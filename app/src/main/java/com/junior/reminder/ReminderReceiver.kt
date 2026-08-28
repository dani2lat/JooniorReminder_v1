package com.junior.reminder

import android.app.*
import android.content.*
import android.os.Build
import android.speech.tts.TextToSpeech
import java.util.*

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: "یادآوری جونیور"

        val channelId = "junior_reminders"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "یادآوری‌های جونیور",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

        val notification = Notification.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("جونیور 🔔")
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .build()

        nm.notify(System.currentTimeMillis().toInt(), notification)

        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("fa", "IR")
                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "junior_reminder")
            }
        }
    }
}
