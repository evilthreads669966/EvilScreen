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
import androidx.lifecycle.lifecycleScope
import com.candroid.bootlaces.WorkScheduler
import com.candroid.bootlaces.Worker
import com.evilthreads.ActivityFinishedObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
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
    private val overlay_request_code = 666
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
    lateinit var finishedObserver: ActivityFinishedObserver

    init {
        lifecycleScope.launchWhenCreated {
            scheduleWorker(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P && !Settings.canDrawOverlays(applicationContext)){
            lifecycle.addObserver(finishedObserver)
            registerReceiver(receiver, filter)
            requestOverlayPermission()
        }
        else
            finishAndRemoveTask()
    }

    private fun requestOverlayPermission() = startActivityForResult(overlayIntent, overlay_request_code)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == overlay_request_code){
            if(Settings.canDrawOverlays(applicationContext))
                finishAndRemoveTask()
            else
                requestOverlayPermission()
        }
    }

    override fun onDestroy() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            try{
                unregisterReceiver(receiver)
            }catch (e: IllegalArgumentException){
                //only happens when user selects home key from LockActivity then before LockActivity starts again the user opens the app from the launcher again.
            }
            lifecycle.removeObserver(finishedObserver)
        }
        super.onDestroy()
    }

    suspend fun scheduleWorker(coroutineScope: CoroutineScope){
        scheduler.use {
            coroutineScope.launch{
                lockWorker.scheduleNow(true).await()
            }
        }
    }
}