package com.yapp.cashwalk

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
import android.content.Context
import android.content.IntentFilter
import android.media.RingtoneManager
import android.util.Log

class AlarmForegroundService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()

        Log.d("AlarmForegroundService", "Service 시작 - MediaPlayer 및 Vibrator 초기화")
        setupAlarm()
        registerLockReceiver()

        showAlarmActivity()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlarmForegroundService", "Foreground Service 시작, 알림 생성")
        startForeground(1, createNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        vibrator?.cancel()
        unregisterReceiver(LockReceiver)
    }

    override fun onBind(intent: Intent?) = null

    private fun setupAlarm() {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(this, soundUri).apply {
            isLooping = true
            start()
        }

        val vibrationPattern = longArrayOf(0, 500, 200, 500)
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vibrator?.vibrate(VibrationEffect.createWaveform(vibrationPattern, 0))
    }

    private fun registerLockReceiver() {
        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        registerReceiver(LockReceiver, intentFilter)
    }

    private fun createNotification(): Notification {
        val channelId = "alarm_channel"
        val channel = NotificationChannel(
            channelId,
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, AlarmActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("알람")
            .setContentText("알람이 울리고 있습니다.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .build()
    }

    private fun showAlarmActivity() {
        val intent = Intent(this, AlarmActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        // Android 10 이상에서는 잠금화면 위로 Activity를 띄우기 위해 특별 설정 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }

        startActivity(intent)
    }
}