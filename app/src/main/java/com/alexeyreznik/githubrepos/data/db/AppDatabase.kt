package com.alexeyreznik.githubrepos.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.alexeyreznik.githubrepos.data.models.Repo


/**
 * Created by alexeyreznik on 24/01/2018.
 */
@Database(entities = [(Repo::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
}