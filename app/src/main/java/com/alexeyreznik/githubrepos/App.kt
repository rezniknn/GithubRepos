package com.alexeyreznik.githubrepos

import android.app.Application
import com.alexeyreznik.githubrepos.di.AppComponent
import com.alexeyreznik.githubrepos.di.AppModule
import com.alexeyreznik.githubrepos.di.DaggerAppComponent
import timber.log.Timber

/**
 * Created by alexeyreznik on 23/01/2018.
 */
class App : Application() {

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        Timber.plant(Timber.DebugTree())
    }
}