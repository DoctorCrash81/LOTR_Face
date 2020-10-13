package com.wizl.beautyscanner

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.wizl.beautyscanner.ui.MainActivity

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Toast.makeText(context, "Ok", Toast.LENGTH_LONG).show()

        if (context != null) {

            val resultIntent = Intent(context, MainActivity::class.java)
            val resultPendingIntent = PendingIntent.getActivity(
                context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder: NotificationCompat.Builder = NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle("Сообщение")
                .setContentText("Ваш индекс красоты зависит от ракурса")
                .setContentIntent(resultPendingIntent)

            val notification: Notification = builder.build()

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notification)
        }

    }


}