/*Copyright 2021 Chris Basinger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package com.evilthreads.ui

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.candroid.bootlaces.WorkScheduler
import com.candroid.bootlaces.Worker
import com.evilthreads.ActivityObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/*
            (   (                ) (             (     (
            )\ ))\ )    *   ) ( /( )\ )     (    )\ )  )\ )
 (   (   ( (()/(()/(  ` )  /( )\()|()/((    )\  (()/( (()/(
 )\  )\  )\ /(_))(_))  ( )(_)|(_)\ /(_))\((((_)( /(_)) /(_))
((_)((_)((_|_))(_))   (_(_()) _((_|_))((_))\ _ )(_))_ (_))
| __\ \ / /|_ _| |    |_   _|| || | _ \ __(_)_\(_)   \/ __|
| _| \ V /  | || |__    | |  | __ |   / _| / _ \ | |) \__ \
|___| \_/  |___|____|   |_|  |_||_|_|_\___/_/ \_\|___/|___/
....................../´¯/)
....................,/¯../
.................../..../
............./´¯/'...'/´¯¯`·¸
........../'/.../..../......./¨¯\
........('(...´...´.... ¯~/'...')
.........\.................'...../
..........''...\.......... _.·´
............\..............(
..............\.............\...
*/
/**
 * @author Chris Basinger
 * @email evilthreads669966@gmail.com
 * @date 05/28/21
 **/
@AndroidEntryPoint
class LauncherActivity: AppCompatActivity(){
    @Inject
    lateinit var receiver: BroadcastReceiver
    @Inject
    lateinit var scheduler: WorkScheduler
    @Inject
    lateinit var lockWorker: Worker
    @Inject
    lateinit var overlayIntent: Intent
    @Inject
    lateinit var filter: IntentFilter
    @Inject
    lateinit var observer: ActivityObserver

    companion object{
        private const val OVERLAY_REQUEST_CODE = 666
        const val ACTION_STOP_ACTIVITY = "ACTION_STOP_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleWorker()
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P && !Settings.canDrawOverlays(applicationContext)){
            lifecycle.addObserver(observer)
            registerReceiver(receiver, filter)
            requestOverlayPermission()
        }
        else
            finishAffinity()
    }

    private fun requestOverlayPermission() = startActivityForResult(overlayIntent, OVERLAY_REQUEST_CODE)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == OVERLAY_REQUEST_CODE){
            if(Settings.canDrawOverlays(applicationContext))
                finishAffinity()
            else
                requestOverlayPermission()
        }
    }

    override fun onDestroy() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            unregisterReceiver(receiver)
            lifecycle.removeObserver(observer)
        }
        super.onDestroy()
    }

    fun scheduleWorker(){
        scheduler.use {
            runBlocking{
                lockWorker.scheduleNow(true).await()
            }
        }
    }
}