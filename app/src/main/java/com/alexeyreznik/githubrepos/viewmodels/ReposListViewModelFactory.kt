package com.alexeyreznik.githubrepos.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.alexeyreznik.githubrepos.data.repositories.ReposRepository

/**
 * Created by alexeyreznik on 23/01/2018.
 */
class ReposListViewModelFactory(private val reposRepository: ReposRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReposListViewModel::class.java)) {
            return ReposListViewModel(reposRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
