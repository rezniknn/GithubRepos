package com.alexeyreznik.githubrepos.data.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.alexeyreznik.githubrepos.data.Repo
import com.alexeyreznik.githubrepos.data.network.GithubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * Created by alexeyreznik on 23/01/2018.
 */
class ReposRepository(private val service: GithubService) {

    fun getUserRepos(username: String): LiveData<List<Repo>> {
        val userRepos = MutableLiveData<List<Repo>>()
        service.getUserRepos(username).enqueue(object : Callback<List<Repo>> {
            override fun onFailure(call: Call<List<Repo>>?, t: Throwable?) {
                Timber.e(t.toString())
                userRepos.value = listOf()
            }

            override fun onResponse(call: Call<List<Repo>>?, response: Response<List<Repo>>?) {
                if (response?.isSuccessful == true) {
                    userRepos.value = response.body()
                } else {
                    Timber.e(response?.message())
                    userRepos.value = listOf()
                }
            }
        })
        return userRepos
    }
}