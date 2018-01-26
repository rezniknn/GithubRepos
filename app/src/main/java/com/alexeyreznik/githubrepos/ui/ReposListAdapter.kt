package com.alexeyreznik.githubrepos.ui

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alexeyreznik.githubrepos.R
import com.alexeyreznik.githubrepos.data.models.Repo
import com.alexeyreznik.githubrepos.utils.ResourcesWrapper

/**
 * Created by alexeyreznik on 23/01/2018.
 */
class ReposListAdapter(private val resourcesWrapper: ResourcesWrapper) : RecyclerView.Adapter<ReposListAdapter.ViewHolder>() {

    val repos = mutableListOf<Repo>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.repo_card, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = repos.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val repo = repos[position]
        holder?.let {
            it.name.text = repo.name
            it.language.text = String.format("%s: %s", resourcesWrapper.getString(R.string.language), repo.language)
            it.forks.text = String.format("%s %d", resourcesWrapper.getString(R.string.forks), repo.forks)
            it.watchers.text = String.format("%s %d", resourcesWrapper.getString(R.string.watchers), repo.forks)
            it.openIssues.text = String.format("%s %d", resourcesWrapper.getString(R.string.open_issues), repo.forks)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.card)
        val name: TextView = itemView.findViewById(R.id.name)
        val language: TextView = itemView.findViewById(R.id.language)
        val forks: TextView = itemView.findViewById(R.id.forks)
        val watchers: TextView = itemView.findViewById(R.id.watchers)
        val openIssues: TextView = itemView.findViewById(R.id.open_issues)
    }
}
