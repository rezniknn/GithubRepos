package com.alexeyreznik.githubrepos.di

import android.app.Application
import com.alexeyreznik.githubrepos.ui.ReposListActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created by alexeyreznik on 23/01/2018.
 */
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(application: Application)
    fun inject(reposListActivity: ReposListActivity)
}