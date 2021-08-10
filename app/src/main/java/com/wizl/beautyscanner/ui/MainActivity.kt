package com.wizl.beautyscanner.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wizl.beautyscanner.NotificationReceiver
import com.wizl.beautyscanner.R
import com.wizl.beautyscanner.logick.UserPersisten
import com.wizl.beautyscanner.logick.analytics.AnalyticsService
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        startTrialPush2()

        AnalyticsService.start(UserPersisten.session)
        UserPersisten.session += 1

        startActivity(Intent(this, GalleryActivity::class.java))
        finish()

    }

    /*
    fun startTrialPush() {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, 5)
        val time: Long = calendar.timeInMillis

        val myIntent = Intent(this, NotificationReceiver::class.java)
//        myIntent.action = Context.NOTIFICATION_SERVICE
        val ALARM1_ID = 0
        val pendingIntent = PendingIntent.getBroadcast(
            this, ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )
    }

    fun startTrialPush2() {
        val alarmIntent = Intent(this, NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this, 0,
            alarmIntent, 0
        )

        val alarmManager =
            getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            20000,
            pendingIntent
        )
        Toast.makeText(this, "Alarm set", Toast.LENGTH_LONG).show()
    }
    */
}

