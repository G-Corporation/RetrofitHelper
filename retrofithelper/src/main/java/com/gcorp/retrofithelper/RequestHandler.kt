package com.gcorp.retrofithelper

import android.util.Log


open class RequestHandler<T> : RequestInterface<T> {
    override fun onBeforeSend() {
        super.onBeforeSend()
    }

    override fun onSuccess(response: Response<T>) {
        super.onSuccess(response)
        onComplete()
    }

    override fun onError(response: Response<T>?) {
        super.onError(response)
        onComplete()
    }

    override fun onFailed(e: Throwable?) {
        super.onFailed(e)
        onComplete()
    }

    override fun onComplete() {
        super.onComplete()
    }
}