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

    private val repos = MediatorLiveData<Resource<List<Repo>>>()
    private val moreRepos = MutableLiveData<Resource<List<Repo>>>()
    private var nextPage: Int = 2
    private var endReached: Boolean = false

    fun getUserRepos(username: String): LiveData<Resource<List<Repo>>> {
        nextPage = 2
        endReached = false
        repos.value = Resource(status = Status.LOADING)
        val dbSource = loadFromDB(username)
        repos.addSource(dbSource, { data ->
            repos.removeSource(dbSource)
            if (!shouldFetch(data)) {
                repos.value = Resource(status = Status.SUCCESS, data = data)
                Timber.d("Database. Items: ${data?.size}")
            } else loadFromNetwork(username)
        })
        return repos
    }

    fun getMoreUserRepos(username: String): LiveData<Resource<List<Repo>>> {
        if (endReached) {
            moreRepos.value = Resource(status = Status.EMPTY)
            return moreRepos
        }
        moreRepos.value = Resource(status = Status.LOADING)
        appExecutors.networkIO.execute {
            service.getUserRepos(username, nextPage, ITEMS_PER_NETWORK_PAGE)
                    .enqueue(object : Callback<List<Repo>> {
                        override fun onFailure(call: Call<List<Repo>>?, t: Throwable?) {
                            Timber.e(t.toString())
                            moreRepos.value = Resource(status = Status.ERROR)
                        }

                        override fun onResponse(call: Call<List<Repo>>?, response: Response<List<Repo>>?) {
                            if (response?.isSuccessful == true) {
                                response.body()?.let {
                                    moreRepos.value = Resource(status = Status.SUCCESS, data = it)
                                    if (it.size < ITEMS_PER_NETWORK_PAGE) endReached = true
                                    else nextPage++
                                    Timber.d("Network. Items: ${it.size}")
                                }
                            } else {
                                Timber.e(response?.message())
                                moreRepos.value = Resource(status = Status.ERROR)
                            }
                        }
                    })
        }
        return moreRepos
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
        repos.addSource(networkSource, { data ->
            repos.removeSource(networkSource)
            if (data != null) {
                appExecutors.diskIO.execute { saveToDb(data) }
                repos.value = Resource(data = data, status = Status.SUCCESS)
                Timber.d("Network. Items: ${data.size}")
            } else repos.value = Resource(status = Status.ERROR)
        })

        appExecutors.networkIO.execute {
            service.getUserRepos(username, 1, ITEMS_PER_NETWORK_PAGE)
                    .enqueue(object : Callback<List<Repo>> {
                        override fun onFailure(call: Call<List<Repo>>?, t: Throwable?) {
                            Timber.e(t.toString())
                            networkSource.value = null
                        }

                        override fun onResponse(call: Call<List<Repo>>?, response: Response<List<Repo>>?) {
                            if (response?.isSuccessful == true) {
                                networkSource.value = response.body()
                                response.body()?.size?.let { if (it < ITEMS_PER_NETWORK_PAGE) endReached = true }
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
        const val ITEMS_PER_NETWORK_PAGE = 30
    }
}