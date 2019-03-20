package com.gcorp.retrofithelper


open class RequestHandler<T> : RequestInterface<T> {
    override fun onBeforeSend() {
        super.onBeforeSend()
    }

    override fun onSuccess(response: Response<T>) {
        super.onSuccess(response)
    }

    override fun onComplete() {
        super.onComplete()
    }

    override fun onError(response: Response<T>?) {
        super.onError(response)
    }

    override fun onFailed(e: Throwable?) {
        super.onFailed(e)
    }
}