package com.evilthreads

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.evilthreads.ui.LauncherActivity


class StopActivityReceiver(private val activity: AppCompatActivity): BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null)
            if(intent.action.equals(LauncherActivity.ACTION_STOP_ACTIVITY))
                activity.finishAffinity()
    }
}