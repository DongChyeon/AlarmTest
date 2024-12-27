package com.yapp.cashwalk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

object LockReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                navigateToLock(context)
            }
        }
    }

    private fun navigateToLock(context: Context) {
        context.startActivity(
            Intent(context, AlarmActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            }
        )
    }
}