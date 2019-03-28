package com.gcorp.retrofithelper

import android.util.Log
import com.google.gson.Gson
import retrofit2.Response

class Response<T>(response: Response<T>, classOfT: Class<T>) {
    var header: MutableMap<String, MutableList<String>>? = response.headers().toMultimap()
    var body = Gson().fromJson<T>(response.body().toString(), classOfT)
    var code = response.code()
    var raw: okhttp3.Response? = response.raw()

}