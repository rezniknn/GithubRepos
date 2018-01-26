package com.alexeyreznik.githubrepos.data.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.alexeyreznik.githubrepos.data.db.AppDatabase
import com.alexeyreznik.githubrepos.data.models.Repo
import com.alexeyreznik.githubrepos.data.models.Resource
import com.alexeyreznik.githubrepos.data.models.Status
import com.alexeyreznik.githubrepos.data.network.GithubService
import com.alexeyreznik.githubrepos.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


/**
 * Created by alexeyreznik on 23/01/2018.
 */
class ReposRepository(private val appExecutors: AppExecutors,
                      private val appDatabase: AppDatabase,
                      private val service: GithubService) {

    private val result = MediatorLiveData<Resource<List<Repo>>>()

    fun getUserRepos(username: String): LiveData<Resource<List<Repo>>> {
        result.value = Resource(status = Status.LOADING)
        val dbSource = loadFromDB(username)
        result.addSource(dbSource, { data ->
            result.removeSource(dbSource)
            if (!shouldFetch(data)) {
                result.value = Resource(status = Status.SUCCESS, data = data)
                Timber.d("Database. Items: ${data?.size}")
            } else loadFromNetwork(username)
        })
        return result
    }

    private fun shouldFetch(data: List<Repo>?): Boolean {
        if (data == null || data.isEmpty()) return true
        val lastInserted = data[0].lastInserted
        return (System.currentTimeMillis() - lastInserted > FETCH_TIMEOUT)
    }

    private fun loadFromDB(username: String): LiveData<List<Repo>> {
        return appDatabase.repoDao().getByName(username)
    }

    private fun loadFromNetwork(username: String) {
        val networkSource = MutableLiveData<List<Repo>>()
        result.addSource(networkSource, { data ->
            result.removeSource(networkSource)
            if (data != null) {
                appExecutors.diskIO.execute { saveToDb(data) }
                result.value = Resource(data = data, status = Status.SUCCESS)
                Timber.d("Network. Items: ${data.size}")
            } else result.value = Resource(status = Status.ERROR)
        })

        appExecutors.networkIO.execute {
            service.getUserRepos(username).enqueue(object : Callback<List<Repo>> {
                override fun onFailure(call: Call<List<Repo>>?, t: Throwable?) {
                    Timber.e(t.toString())
                    networkSource.value = null
                }

                override fun onResponse(call: Call<List<Repo>>?, response: Response<List<Repo>>?) {
                    if (response?.isSuccessful == true) {
                        networkSource.value = response.body()
                    } else {
                        Timber.e(response?.message())
                        networkSource.value = null
                    }
                }
            })
        }
    }

    private fun saveToDb(data: List<Repo>) {
        data.map { it.lastInserted = System.currentTimeMillis() }
        appDatabase.repoDao().insertAll(data)
    }

    companion object {
        const val FETCH_TIMEOUT = 60 * 1000L //60 seconds fetch timeout
    }
}