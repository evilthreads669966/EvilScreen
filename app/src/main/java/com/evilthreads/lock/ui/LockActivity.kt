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
package com.evilthreads.lock.ui

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.evilthreads.IntentFactory
import com.evilthreads.ActivityObserver
import com.evilthreads.lock.AppIconState
import com.evilthreads.lock.LockState
import dagger.hilt.android.AndroidEntryPoint
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
 * @date 01/07/21
 **/
@AndroidEntryPoint
internal class LockActivity: AppCompatActivity(){
    @Inject
    lateinit var launcherName: ComponentName
    @Inject
    lateinit var lockState: LockState
    @Inject
    lateinit var appIconState: AppIconState
    @Inject
    lateinit var intentFactory: IntentFactory
    @Inject
    lateinit var activityObserver: ActivityObserver

    override fun onResume() {
        super.onResume()
        lockState.setLocked()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            hideAppIcon()
        else if(activityObserver.isActivityStarted()){
            val intent = intentFactory.createFinishActivityIntent()
            sendBroadcast(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        lockState.setUnlocked()
        finishAndRemoveTask()
    }

    private fun hideAppIcon(){
        if(appIconState.isVisible()){
            packageManager.setComponentEnabledSetting(launcherName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
            appIconState.setHidden()
        }
    }
}