package com.alexeyreznik.githubrepos.data

import com.squareup.moshi.Json

/**
 * Created by alexeyreznik on 23/01/2018.
 */
data class Repo(@Json(name = "id") val id: String,
                @Json(name = "name") val name: String,
                @Json(name = "description") val description: String,
                @Json(name = "language") val language: String)