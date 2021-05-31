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
package com.evilthreads

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.evilthreads.lock.LockAction
import com.evilthreads.lock.ui.LockActivity
import com.evilthreads.ui.LauncherActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

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
@Singleton
class IntentFactory @Inject constructor(@ApplicationContext private val ctx: Context, private val lockAction: LockAction, private val appUri: Uri){

    fun createActivityIntent() = Intent().apply {
        setClass(ctx, LockActivity::class.java)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    fun createBroadcastIntent() = Intent(lockAction.name)

    fun createOverlayIntent() =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, appUri)

    fun createFinishActivityIntent() = Intent(FinishActivityReceiver.ACTION_FINISH_ACTIVITY)
}