package com.alexeyreznik.githubrepos.data.models

/**
 * Created by alexeyreznik on 23/01/2018.
 */
data class Resource<out T>(val status: Status = Status.SUCCESS, val data: T? = null)