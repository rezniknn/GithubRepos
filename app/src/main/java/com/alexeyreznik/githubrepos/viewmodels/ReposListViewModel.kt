package com.alexeyreznik.githubrepos.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.alexeyreznik.githubrepos.data.Repo
import com.alexeyreznik.githubrepos.data.repositories.ReposRepository
import javax.inject.Inject

/**
 * Created by alexeyreznik on 23/01/2018.
 */

class ReposListViewModel @Inject constructor(private val reposRepository: ReposRepository) : ViewModel() {

    private val usernameLiveData = MutableLiveData<String>()
    var reposListLiveData: LiveData<List<Repo>>

    init {
        reposListLiveData = Transformations.switchMap(usernameLiveData,
                { username -> reposRepository.getUserRepos(username) })
    }

    fun setUsername(username: String) {
        usernameLiveData.value = username
    }
}