package com.gcorp.retrofithelper


interface RequestInterface<T> {
    fun onBeforeSend() {}
    fun onSuccess(response: Response<T>) {}
    fun onComplete() {}
    fun onError(response: Response<T>?) {}
    fun onFailed(e: Throwable?) {}
}
