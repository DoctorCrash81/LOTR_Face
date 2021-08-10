package com.wizl.lookalike.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wizl.lookalike.R
import com.wizl.lookalike.logick.UserPersisten
import com.wizl.lookalike.logick.analytics.AnalyticsService


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AnalyticsService.start(UserPersisten.session)
        UserPersisten.session += 1

        startActivity(Intent(this, GalleryActivity::class.java))
        finish()

    }
}

