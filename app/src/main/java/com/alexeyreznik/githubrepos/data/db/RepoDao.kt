package com.alexeyreznik.githubrepos.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.alexeyreznik.githubrepos.data.models.Repo


/**
 * Created by alexeyreznik on 24/01/2018.
 */
@Dao
interface RepoDao {
    @Query("SELECT * FROM repo WHERE upper(login) = upper(:username)")
    fun getByName(username: String): LiveData<List<Repo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(repos: List<Repo>)
}