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
package com.evilthreads.lock
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.evilthreads.IntentFactory
import com.evilthreads.LockManagerEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
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
class LockManagerImpl @Inject constructor(intentFactory: IntentFactory, @ApplicationContext private val ctx: Context, private val powerMgr: PowerManager, private val keyguardMgr: KeyguardManager, private val lockState: LockState): LockManager {
    private val activityIntent: Intent = intentFactory.createActivityIntent()
    private val broadcastIntent: Intent = intentFactory.createBroadcastIntent()

    companion object {
        @Volatile
        private var INSTANCE: LockManager? = null
       
        fun getInstance(ctx: Context): LockManager {
            return INSTANCE ?: synchronized(this) {
                val entryPoint = EntryPointAccessors.fromApplication(ctx.applicationContext, LockManagerEntryPoint::class.java)
                INSTANCE = entryPoint.getLockManager()
                INSTANCE!!
            }
        }
    }

    override fun isLockable(): Boolean{
        var overlay = true
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
            overlay = Settings.canDrawOverlays(ctx)
        return overlay && lockState.isUnlocked()
                && !keyguardMgr.isKeyguardLocked
                && powerMgr.isInteractive
    }

    override fun lock() {
        if(isLockable())
            ctx.startActivity(activityIntent)
    }

    override suspend fun broadcast() {
        if(powerMgr.isInteractive)
            ctx.sendBroadcast(broadcastIntent)
    }

    override fun setLocked() = lockState.setLocked()

    override fun setUnlocked() = lockState.setUnlocked()
}

interface LockManager {
    fun isLockable(): Boolean

    fun lock()

    suspend fun broadcast()

    fun setLocked()

    fun setUnlocked()
}
