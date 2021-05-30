package com.evilthreads

import androidx.lifecycle.Lifecycle.*
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityObserver @Inject constructor(): LifecycleObserver {
    private var activityStarted: Boolean = false

    @OnLifecycleEvent(Event.ON_CREATE)
    private fun setStarted(){
        activityStarted = true
    }

    @OnLifecycleEvent(Event.ON_DESTROY)
    private fun setStopped(){
        activityStarted = false
    }

    fun isActivityStarted() = activityStarted
}