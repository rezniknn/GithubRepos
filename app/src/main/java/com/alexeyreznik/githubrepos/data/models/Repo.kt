package com.alexeyreznik.githubrepos.data.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.squareup.moshi.Json

/**
 * Created by alexeyreznik on 23/01/2018.
 */
@Entity(tableName = "repo")
data class Repo(@Json(name = "id") @PrimaryKey val id: String,
                @Json(name = "name") val name: String,
                @Json(name = "language") val language: String?,
                @Json(name = "description") val description: String?,
                @Json(name = "forks") val forks: Int = 0,
                @Json(name = "watchers") val watchers: Int = 0,
                @Json(name = "open_issues") val openIssues: Int = 0,
                @Json(name = "owner") @Embedded val owner: Owner,
                var lastInserted: Long) {

    data class Owner(@Json(name = "login") val login: String,
                     @Json(name = "avatar_url") val avatarUrl: String)
}