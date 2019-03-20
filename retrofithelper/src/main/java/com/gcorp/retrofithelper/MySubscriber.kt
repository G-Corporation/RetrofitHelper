package com.gcorp.retrofithelper

import android.util.Log
import retrofit2.Response
import rx.Subscriber

abstract class MySubscriber<T>(private val requestHandler: RequestHandler<T>?): Subscriber<Response<T>>() {
    override fun onNext(t: Response<T>?) {
        if (t != null) {
            Log.e("myApp", "12")
            Log.e("myApp", "code -> ${t.code()}")
            Log.e("myApp", "body -> ${t.body()}")
            Log.e("myApp", "header -> ${t.headers()}")

        }

        if (t == null) {
            requestHandler?.onFailed(Throwable("Response in null"))
            return
        }

        val res = Response(t)

        if (t.code() != 200) {
            Log.e("myApp", "13")
            requestHandler?.onError(res)
            return
        }
        Log.e("myApp", "14")
        requestHandler?.onSuccess(res)

    }

    override fun onCompleted() {
        Log.e("myApp", "onCompleted()")
        requestHandler?.onComplete()
    }

    override fun onError(e: Throwable?) {
        Log.e("myApp", "onError()")
        requestHandler?.onFailed(e)
        e?.printStackTrace()
    }
}