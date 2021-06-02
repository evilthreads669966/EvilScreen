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

import android.app.KeyguardManager
import android.content.*
import android.net.Uri
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity
import com.candroid.bootlaces.Worker
import com.evilthreads.lock.LockManager
import com.evilthreads.lock.LockAction
import com.evilthreads.lock.LockManagerImpl
import com.evilthreads.lock.background.LockWorker
import com.evilthreads.ui.LauncherActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
@Module
@InstallIn(SingletonComponent::class)
object AppModule{
    @Provides
    fun provideLauncherComponent(): Class<*> = LauncherActivity::class.java
   
    @Singleton
    @Provides
    fun provideLauncherComponentName(@ApplicationContext ctx: Context, launcherComponent: Class<*>): ComponentName = ComponentName(ctx, launcherComponent)
    
    @Provides
    fun providePowerManager(@ApplicationContext ctx: Context): PowerManager = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
   
    @Provides
    fun provideKeyguardManager(@ApplicationContext ctx: Context): KeyguardManager = ctx.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
   
    @Provides
    fun provideLockAction(): LockAction = LockAction.getInstance()

    @Provides
    fun provideAppUri(@ApplicationContext ctx: Context): Uri = Uri.parse("package:${ctx.packageName}")
}

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule{
    @Provides
    fun provideIntentFilter(): IntentFilter = IntentFilter(FinishActivityReceiver.ACTION_FINISH_ACTIVITY)

    @Provides
    fun provideOverlayIntent(intentFactory: IntentFactory): Intent = intentFactory.createOverlayIntent()

    @Provides
    fun provideReceiver(@ActivityContext ctx: Context): BroadcastReceiver = FinishActivityReceiver(ctx as AppCompatActivity)

    @Provides
    fun provideWorker(): Worker = LockWorker()
}

@Module
@InstallIn(SingletonComponent::class)
interface AppBindings{
    @Binds
    fun bindLockManager(mgr: LockManagerImpl): LockManager
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface LockManagerEntryPoint{
    fun getLockManager(): LockManager
}
