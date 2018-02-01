package com.alexeyreznik.githubrepos.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.alexeyreznik.githubrepos.data.models.Repo
import com.alexeyreznik.githubrepos.data.models.Resource
import com.alexeyreznik.githubrepos.data.repositories.ReposRepository

/**
 * Created by alexeyreznik on 23/01/2018.
 */

class ReposListViewModel(private val reposRepository: ReposRepository) : ViewModel() {

    val usernameLiveData = MutableLiveData<String>()
    val loadingMoreLiveData = MutableLiveData<Boolean>()
    var reposLiveData: LiveData<Resource<List<Repo>>>
    var moreReposLiveData: LiveData<Resource<List<Repo>>?>

    init {
        loadingMoreLiveData.value = false
        reposLiveData = Transformations.switchMap(usernameLiveData,
                { username -> reposRepository.getUserRepos(username) })
        moreReposLiveData = Transformations.switchMap(loadingMoreLiveData,
                { loadMore ->
                    if (loadMore) usernameLiveData.value?.let { reposRepository.getMoreUserRepos(it) }
                    else null
                })
    }

}