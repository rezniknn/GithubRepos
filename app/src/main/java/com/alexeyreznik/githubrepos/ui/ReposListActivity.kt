package com.alexeyreznik.githubrepos.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.alexeyreznik.githubrepos.App
import com.alexeyreznik.githubrepos.R
import com.alexeyreznik.githubrepos.data.Repo
import com.alexeyreznik.githubrepos.utils.SharedPrefs
import com.alexeyreznik.githubrepos.viewmodels.ReposListViewModel
import com.alexeyreznik.githubrepos.viewmodels.ReposListViewModelFactory
import kotlinx.android.synthetic.main.activity_repos_list.*
import javax.inject.Inject


class ReposListActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ReposListViewModelFactory
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private lateinit var adapter: ReposListAdapter
    private lateinit var viewModel: ReposListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repos_list)
        title = getString(R.string.repos)

        (application as App).component.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ReposListViewModel::class.java)
        viewModel.reposListLiveData.observe(this, Observer { setUserRepos(it) })

        initUi()
        refresh()
    }

    private fun initUi() {
        adapter = ReposListAdapter()
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(applicationContext)

        val usernameValue = sharedPrefs.getString(SharedPrefs.KEY_USERNAME, getString(R.string.default_username))
        username.setText(usernameValue)
        username.setSelection(usernameValue.length)

        srl.setOnRefreshListener { refresh() }
        refresh.setOnClickListener { refresh() }
    }

    private fun refresh() {
        if (username.text.isEmpty()) {
            username.error = getString(R.string.error_empty_username)
            username.requestFocus()
            return
        }
        viewModel.setUsername(username.text.toString())
        sharedPrefs.putString(SharedPrefs.KEY_USERNAME, username.text.toString())
        srl.isRefreshing = true
    }

    private fun setUserRepos(userRepos: List<Repo>?) {
        userRepos?.let {
            adapter.repos.clear()
            adapter.repos.addAll(userRepos)
            adapter.notifyDataSetChanged()
            srl.isRefreshing = false
        }
    }
}
