package com.yapp.cashwalk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlarmReceiver", "알람 수신, Foreground Service 시작")
        val serviceIntent = Intent(context, AlarmForegroundService::class.java)
        context.startForegroundService(serviceIntent)
    }
}
