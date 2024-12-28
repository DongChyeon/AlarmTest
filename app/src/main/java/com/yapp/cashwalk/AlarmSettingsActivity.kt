package com.yapp.cashwalk

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yapp.cashwalk.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_settings)

        checkExactAlarmPermission()
        checkAndRequestPermissions()

        if (PermissionUtil.alertPermissionCheck(this)) {
            PermissionUtil.onObtainingPermissionOverlayWindow(this)
        }

        findViewById<Button>(R.id.select_sound_button).setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알람 소리 선택")
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                    AlarmPreferences.getSelectedSoundUri(this@AlarmSettingsActivity)
                )
            }
            startActivityForResult(intent, SOUND_PICKER_REQUEST_CODE)
        }

        findViewById<Button>(R.id.select_vibration_button).setOnClickListener {
            val vibrationPatterns = listOf(
                "짧은 진동" to longArrayOf(0, 200, 100, 200),
                "긴 진동" to longArrayOf(0, 500, 200, 500),
                "강한 진동" to longArrayOf(0, 1000, 500, 1000)
            )

            val items = vibrationPatterns.map { it.first }.toTypedArray()
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("진동 패턴 선택")
                .setItems(items) { _, which ->
                    val selectedPattern = vibrationPatterns[which].second
                    AlarmPreferences.saveSelectedVibrationPattern(this, selectedPattern)
                    Toast.makeText(this, "${items[which]} 선택됨", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        findViewById<Button>(R.id.complete_button).setOnClickListener {
            Toast.makeText(this, "설정이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.set_test_alarm_button).setOnClickListener {
            setTestAlarm(this)
            Toast.makeText(this, "10초 후 테스트 알람이 설정되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.VIBRATE)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = permissions.zip(grantResults.toTypedArray())
                .filter { it.second != PackageManager.PERMISSION_GRANTED }
                .map { it.first }

            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "일부 권한이 거부되었습니다. 앱이 제대로 작동하지 않을 수 있습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SOUND_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri: Uri? = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                AlarmPreferences.saveSelectedSoundUri(this, uri)
                Toast.makeText(this, "알람 소리가 선택되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val SOUND_PICKER_REQUEST_CODE = 1001
        private const val PERMISSION_REQUEST_CODE = 1002
    }
}
