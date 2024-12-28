package com.yapp.cashwalk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        findViewById<Button>(R.id.btn_dismiss_alarm).setOnClickListener {
            stopAlarm()
            finish()
        }

        findViewById<Button>(R.id.btn_snooze_alarm).setOnClickListener {
            val snoozeTime = System.currentTimeMillis() + 5 * 60 * 1000
            setAlarm(this, snoozeTime)
            stopAlarm()
            finish()
        }
    }

    private fun stopAlarm() {
        val intent = Intent(this, AlarmForegroundService::class.java)
        stopService(intent)
    }
}