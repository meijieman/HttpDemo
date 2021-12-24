package com.major.http

import com.google.gson.Gson

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/12/20 5:08 下午
 */
val gson = Gson()

fun Any?.toJsonString(): String {

    return if (this != null)
        gson.toJson(this)
    else
        ""
}