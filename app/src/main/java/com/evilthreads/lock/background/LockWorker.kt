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

package com.evilthreads.lock.background

import android.content.Context
import android.content.Intent
import com.candroid.bootlaces.Worker
import com.evilthreads.lock.LockAction
import com.evilthreads.lock.LockManager
import kotlinx.coroutines.delay

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
class LockWorker:  Worker(666, "pawning", true){
    override val receiver: WorkReceiver
        get() = object: WorkReceiver(LockAction.getInstance().name){
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if(ctx != null && intent != null)
                    if(intent.action.equals(LockAction.getInstance().name))
                        LockManager.getInstance(ctx).lock()
            }
        }

    override suspend fun doWork(ctx: Context){
        while(true){
            LockManager.getInstance(ctx).broadcast()
            delay(500)
        }
    }
}