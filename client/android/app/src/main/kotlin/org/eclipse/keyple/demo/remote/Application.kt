package org.eclipse.keyple.demo.remote

import android.content.Context
import androidx.multidex.MultiDex
import dagger.android.DaggerApplication
import org.eclipse.keyple.demo.remote.di.AppComponent
import org.eclipse.keyple.demo.remote.di.DaggerAppComponent
import timber.log.Timber
import timber.log.Timber.DebugTree

class Application : DaggerApplication() {
    override fun attachBaseContext(context: Context?) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
    }

    override fun applicationInjector(): AppComponent? {
        return DaggerAppComponent.builder().application(this).build()
    }
}