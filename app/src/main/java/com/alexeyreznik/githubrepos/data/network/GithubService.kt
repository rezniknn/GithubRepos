package com.alexeyreznik.githubrepos.data.network

import com.alexeyreznik.githubrepos.data.models.Repo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by alexeyreznik on 23/01/2018.
 */
interface GithubService {

    @GET("/users/{username}/repos")
    fun getUserRepos(@Path("username") username: String,
                     @Query("page") page: Int = 1,
                     @Query("per_page") perPage: Int = 30): Call<List<Repo>>
}