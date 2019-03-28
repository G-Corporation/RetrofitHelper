package com.gcorp.retrofithelper

import android.util.JsonReader
import android.util.Log
import com.google.gson.Gson
import retrofit2.Response
import java.io.StringReader

class Response<T>(response: Response<T>, classOfT: Class<T>) {
    var header: MutableMap<String, MutableList<String>>? = response.headers().toMultimap()
    var body = Gson().fromJson<T>(Gson().toJson(response.body()), classOfT)
    var code = response.code()
    var raw: okhttp3.Response? = response.raw()

}