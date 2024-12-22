package com.yapp.cashwalk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        Log.d("AlarmActivity", "Activity 시작, 잠금 화면 표시 설정")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

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