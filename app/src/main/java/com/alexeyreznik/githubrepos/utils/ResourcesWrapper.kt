package com.alexeyreznik.githubrepos.utils

import android.content.Context

/**
 * Created by alexeyreznik on 25/01/2018.
 */
class ResourcesWrapper(val context: Context) {

    fun getString(resourceId: Int): String = context.getString(resourceId)
}