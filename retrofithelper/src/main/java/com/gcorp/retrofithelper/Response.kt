package com.gcorp.retrofithelper

import android.util.Log
import retrofit2.Response

class Response<T>(response: Response<T>) {
    var header = response.headers().toMultimap()
    var body = response.body()
    var code = response.code()

}