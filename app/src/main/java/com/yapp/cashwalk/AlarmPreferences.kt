package com.yapp.cashwalk

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

object AlarmPreferences {

    private const val PREFS_NAME = "alarm_preferences"
    private const val SOUND_URI_KEY = "sound_uri"
    private const val VIBRATION_PATTERN_KEY = "vibration_pattern"

    private fun getPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSelectedSoundUri(context: Context, uri: Uri) {
        getPreferences(context).edit().putString(SOUND_URI_KEY, uri.toString()).apply()
    }

    fun getSelectedSoundUri(context: Context): Uri? {
        val uriString = getPreferences(context).getString(SOUND_URI_KEY, null)
        return uriString?.let { Uri.parse(it) }
    }

    fun saveSelectedVibrationPattern(context: Context, pattern: LongArray) {
        getPreferences(context).edit().putString(VIBRATION_PATTERN_KEY, pattern.joinToString(",")).apply()
    }

    fun getSelectedVibrationPattern(context: Context): LongArray? {
        val patternString = getPreferences(context).getString(VIBRATION_PATTERN_KEY, null)
        return patternString?.split(",")?.map { it.toLong() }?.toLongArray()
    }
}