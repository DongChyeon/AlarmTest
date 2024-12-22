package com.yapp.cashwalk

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast

fun setAlarm(context: Context, triggerTime: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(
                context,
                "정확한 알람 권한을 활성화하세요.",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return
        } else {
            Log.d("setAlarm", "정확한 알람 권한이 허용되었습니다.")
        }
    }

    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.setExact(
        AlarmManager.RTC_WAKEUP,
        triggerTime,
        pendingIntent
    )
    Log.d("setAlarm", "알람 설정 완료. 시간: $triggerTime")
}

fun setTestAlarm(context: Context) {
    val alarmTime = System.currentTimeMillis() + 30 * 1000
    setAlarm(context, alarmTime)
}

//
//private fun isRunningOnEmulator(): Boolean {
//    val product = Build.PRODUCT
//    return product != null && (product.contains("sdk") || product.contains("emulator"))
//}