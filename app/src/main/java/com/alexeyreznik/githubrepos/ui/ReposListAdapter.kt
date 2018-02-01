package com.alexeyreznik.githubrepos.ui

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.alexeyreznik.githubrepos.R
import com.alexeyreznik.githubrepos.data.models.Repo
import com.alexeyreznik.githubrepos.utils.ResourcesWrapper

/**
 * Created by alexeyreznik on 23/01/2018.
 */
class ReposListAdapter(private val resourcesWrapper: ResourcesWrapper) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val repos = mutableListOf<Repo?>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.repo_card, parent, false))
            VIEW_TYPE_PROGRESS -> ProgressViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.repo_card_loading, parent, false))
            else -> throw Exception("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val repo = repos[position]
        when (holder?.itemViewType) {
            VIEW_TYPE_ITEM -> {
                with(holder as ItemViewHolder) {
                    name.text = repo?.name
                    language.text = String.format("%s: %s", resourcesWrapper.getString(R.string.language), repo?.language)
                    forks.text = String.format("%s %d", resourcesWrapper.getString(R.string.forks), repo?.forks)
                    watchers.text = String.format("%s %d", resourcesWrapper.getString(R.string.watchers), repo?.forks)
                    openIssues.text = String.format("%s %d", resourcesWrapper.getString(R.string.open_issues), repo?.forks)
                }
            }
        }
    }

    override fun getItemCount(): Int = repos.size

    override fun getItemViewType(position: Int): Int =
            if (repos[position] == null) VIEW_TYPE_PROGRESS
            else VIEW_TYPE_ITEM

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.card)
        val name: TextView = itemView.findViewById(R.id.name)
        val language: TextView = itemView.findViewById(R.id.language)
        val forks: TextView = itemView.findViewById(R.id.forks)
        val watchers: TextView = itemView.findViewById(R.id.watchers)
        val openIssues: TextView = itemView.findViewById(R.id.open_issues)
    }

    inner class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loading: ProgressBar = itemView.findViewById(R.id.loading)
    }

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_PROGRESS = 1
    }
}
