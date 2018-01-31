package com.alexeyreznik.githubrepos.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.alexeyreznik.githubrepos.data.db.AppDatabase
import com.alexeyreznik.githubrepos.data.network.GithubService
import com.alexeyreznik.githubrepos.data.repositories.ReposRepository
import com.alexeyreznik.githubrepos.utils.AppExecutors
import com.alexeyreznik.githubrepos.utils.ResourcesWrapper
import com.alexeyreznik.githubrepos.utils.SharedPrefs
import com.alexeyreznik.githubrepos.viewmodels.ReposListViewModelFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


/**
 * Created by alexeyreznik on 23/01/2018.
 */
@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    @Singleton
    fun provideSharedPrefs(context: Context): SharedPrefs = SharedPrefs(context)

    @Provides
    @Singleton
    fun provideResourcesWrapper(context: Context): ResourcesWrapper = ResourcesWrapper(context)

    @Provides
    @Singleton
    fun provideAppExecutors(): AppExecutors = AppExecutors()

    @Provides
    @Singleton
    fun provideGithubService(): GithubService = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GithubService::class.java)

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase = Room.databaseBuilder(context,
            AppDatabase::class.java, "main")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUserReposRepository(appExecutors: AppExecutors, appDatabase: AppDatabase, service: GithubService): ReposRepository = ReposRepository(appExecutors, appDatabase, service)

    @Provides
    fun provideReposListViewModelFactory(reposRepository: ReposRepository) = ReposListViewModelFactory(reposRepository)
}