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
import android.media.RingtoneManager
import android.util.Log

class AlarmForegroundService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()

        val soundUri = AlarmPreferences.getSelectedSoundUri(this)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(this, soundUri)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        val vibrationPattern = AlarmPreferences.getSelectedVibrationPattern(this)
            ?: longArrayOf(0, 500, 200, 500)
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vibrator?.vibrate(VibrationEffect.createWaveform(vibrationPattern, 0))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlarmForegroundService", "Foreground Service 시작, 알림 생성")
        startForeground(1, createNotification())
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        vibrator?.cancel()
    }

    override fun onBind(intent: Intent?) = null

    private fun createNotification(): Notification {
        Log.d("AlarmForegroundService", "Foreground Service 알림 생성")
        val channelId = "alarm_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, AlarmActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
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
}