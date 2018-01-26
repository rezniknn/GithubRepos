package com.alexeyreznik.githubrepos.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.alexeyreznik.githubrepos.App
import com.alexeyreznik.githubrepos.R
import com.alexeyreznik.githubrepos.data.models.Repo
import com.alexeyreznik.githubrepos.data.models.Resource
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
        viewModel.reposListLiveData.observe(this, Observer { updateUi(it) })

        initUi()
        refresh()
    }

    private fun initUi() {
        adapter = ReposListAdapter(resourcesWrapper)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(applicationContext)

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
            showError()
            return
        }
        viewModel.setUsername(username.text.toString())
    }

    private fun updateUi(reposListLiveData: Resource<List<Repo>>?) {
        reposListLiveData?.let {
            when (reposListLiveData.status) {
                Status.LOADING -> showLoading()
                Status.ERROR -> showError()
                Status.SUCCESS -> {
                    if (reposListLiveData.data != null && !reposListLiveData.data.isEmpty()) {
                        adapter.repos.clear()
                        adapter.repos.addAll(reposListLiveData.data)
                        adapter.notifyDataSetChanged()
                        sharedPrefs.putString(SharedPrefs.KEY_USERNAME, username.text.toString())
                        showSuccess()
                    } else showError()
                }
            }
        }
    }

    private fun showLoading() {
        recycler_view.visibility = View.GONE
        no_repos.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    private fun showSuccess() {
        loading.visibility = View.GONE
        no_repos.visibility = View.GONE
        recycler_view.visibility = View.VISIBLE
    }

    private fun showError() {
        loading.visibility = View.GONE
        recycler_view.visibility = View.GONE
        no_repos.visibility = View.VISIBLE
    }

    private fun hideKeyboard() {
        currentFocus?.let {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
