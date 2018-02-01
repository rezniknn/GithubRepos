package com.alexeyreznik.githubrepos.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.alexeyreznik.githubrepos.App
import com.alexeyreznik.githubrepos.R
import com.alexeyreznik.githubrepos.data.models.Status
import com.alexeyreznik.githubrepos.utils.ResourcesWrapper
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
    @Inject
    lateinit var resourcesWrapper: ResourcesWrapper

    private lateinit var adapter: ReposListAdapter
    private lateinit var viewModel: ReposListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repos_list)
        (application as App).component.inject(this)

        title = resourcesWrapper.getString(R.string.repos)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ReposListViewModel::class.java)
        subscribeUi()

        initUi()
        refresh()
    }

    private fun initUi() {
        adapter = ReposListAdapter(resourcesWrapper)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(applicationContext)
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val lastVisibleItem = (recyclerView?.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                val totalItems = recyclerView.adapter.itemCount

                if (totalItems - lastVisibleItem < LOAD_MORE_THRESHOLD &&
                        viewModel.loadingMoreLiveData.value == false) {
                    viewModel.loadingMoreLiveData.value = true
                }
            }
        })

        val usernameValue = sharedPrefs.getString(SharedPrefs.KEY_USERNAME, getString(R.string.default_username))
        username.setText(usernameValue)
        username.setSelection(usernameValue.length)

        username.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                refresh()
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun refresh() {
        if (username.text.isEmpty()) {
            username.error = getString(R.string.error_empty_username)
            username.requestFocus()
            showRefreshError()
            return
        }
        viewModel.usernameLiveData.value = username.text.toString()
        recycler_view.scrollToPosition(0)
    }

    private fun subscribeUi() {
        viewModel.reposLiveData.observe(this, Observer { repos ->
            repos?.let {
                when (repos.status) {
                    Status.LOADING -> showRefreshing()
                    Status.ERROR -> showRefreshError()
                    Status.SUCCESS -> {
                        repos.data?.let {
                            adapter.repos.clear()
                            adapter.repos.addAll(repos.data)
                            adapter.notifyDataSetChanged()
                            sharedPrefs.putString(SharedPrefs.KEY_USERNAME, username.text.toString())
                            showRefreshSuccess()
                        } ?: showRefreshError()
                    }
                    Status.EMPTY -> {
                        showRefreshEmpty()
                    }
                }
            }
        })
        viewModel.moreReposLiveData.observe(this, Observer { moreRepos ->
            moreRepos?.let {
                when (moreRepos.status) {
                    Status.LOADING -> showLoadingMore()
                    Status.ERROR -> {
                        hideLoadingMore()
                        viewModel.loadingMoreLiveData.value = false
                    }
                    Status.SUCCESS -> {
                        hideLoadingMore()
                        moreRepos.data?.let {
                            Handler().post({
                                val initialSize = adapter.repos.size
                                adapter.repos.addAll(moreRepos.data)
                                adapter.notifyItemRangeInserted(initialSize, moreRepos.data.size)
                            })
                        }
                        viewModel.loadingMoreLiveData.value = false
                    }
                    Status.EMPTY -> {
                        viewModel.loadingMoreLiveData.value = false
                    }
                }
            }
        })
    }

    private fun showRefreshing() {
        recycler_view.visibility = View.GONE
        no_repos.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    private fun showRefreshSuccess() {
        loading.visibility = View.GONE
        no_repos.visibility = View.GONE
        recycler_view.visibility = View.VISIBLE
    }

    private fun showRefreshError() {
        loading.visibility = View.GONE
        recycler_view.visibility = View.GONE
        no_repos.visibility = View.GONE
        Toast.makeText(this, getString(R.string.error_failed_to_load_repositories), Toast.LENGTH_LONG).show()
    }

    private fun showRefreshEmpty() {
        loading.visibility = View.GONE
        recycler_view.visibility = View.GONE
        no_repos.visibility = View.VISIBLE
    }

    private fun showLoadingMore() {
        Handler().post({
            adapter.repos.add(null)
            adapter.notifyItemInserted(adapter.repos.size - 1)
        })
    }

    private fun hideLoadingMore() {
        Handler().post({
            adapter.repos.removeAt(adapter.repos.size - 1)
            adapter.notifyItemRemoved(adapter.repos.size)
        })
    }

    private fun hideKeyboard() {
        currentFocus?.let {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    companion object {
        const val LOAD_MORE_THRESHOLD = 5 //number of unseen items left in recyclerview before loading more
    }
}
