package com.gcorp.retrofithelper

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
import java.lang.NullPointerException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

open class RetrofitClient {
    companion object {
        val instance = com.gcorp.retrofithelper.RetrofitClient()
        private val BASE_URL = "RETROFIT_CLIENT_BASE_URL"
        private fun setMap(key: String, value: String): HashMap<String, String> {
            val map = HashMap<String, String>()
            map[key] = value
            return map
        }
    }

    protected val urls = HashMap<String, String>()
    protected var logRequest: Boolean = true
    protected var connectionTimeout: Long = 15
    protected var readingTimeout: Long = 15
    protected var certificate: InputStream? = null
    protected var headers = HashMap<String, String>()
    protected val retrofits = HashMap<String, Retrofit>()
    protected var caching: Boolean = false
    protected var cacheStorageSpace = (5 * 1024 * 1024).toLong()
    protected var cacheTimeLimit = TimeUnit.DAYS.toMillis(7)
    protected var myCache: Cache? = null

    fun setBaseUrl(baseUrl: String): RetrofitClient {
        return setUrl(BASE_URL, baseUrl)
    }


    fun setUrl(keyUrl: String, url: String): RetrofitClient {
        urls[keyUrl] = url
        return this
    }

    fun setLogRequest(logVisibility: Boolean): RetrofitClient {
        logRequest = logVisibility
        return this
    }

    fun setConnectionTimeout(second: Long): RetrofitClient {
        connectionTimeout = second
        return this
    }

    fun setReadingTimeout(second: Long): RetrofitClient {
        readingTimeout = second
        return this
    }

    //Certificate
    fun setCertificate(certificateUri: String): RetrofitClient {
        certificate = FileInputStream(certificateUri)
        return this
    }

    fun setCertificate(fileInputStream: FileInputStream): RetrofitClient {
        certificate = fileInputStream
        return this
    }

    //Header
    fun addHeader(headers: HashMap<String, String>): RetrofitClient {
        this.headers.putAll(headers)
        return this
    }

    fun addHeader(key: String, value: String): RetrofitClient {
        this.headers[key] = value
        return this
    }

    fun removeHeader(headers: HashMap<String, String>): RetrofitClient {
        headers.forEach {
            if (this.headers[it.key] != null) {
                this.headers.remove(it.key)
            }
        }
        return this
    }

    fun removeHeader(key: String, value: String): RetrofitClient {
        headers.remove(key)
        return this
    }

    fun clearHeader(): RetrofitClient {
        this.headers.clear()
        return this
    }

    fun caching(caching: Boolean, context: Context?): RetrofitClient {
        //Todo:context must be not null if caching is true
        if (caching && context != null) {
            return enableCaching(context)
        }
        this.caching = false
        return this
    }

    fun enableCaching(context: Context, cacheStorageSpace: Int = 5, cacheTimeLimit: Long = TimeUnit.DAYS.toMillis(7)): RetrofitClient {
        //Todo:cacheStorageSpace is MG and  cacheTimeLimit is Millisecond
        return setCacheLifeTime(cacheTimeLimit).setCacheStorageSpace(context, cacheStorageSpace)
    }

    fun setCacheStorageSpace(context: Context, cacheStorageSpace: Int): RetrofitClient {
        //Todo:cacheStorageSpace is MG
        this.cacheStorageSpace = (cacheStorageSpace * 1024 * 1024).toLong()
        return setupCaching(context)
    }

    fun setCacheStorageSpaceByte(context: Context, cacheStorageSpace: Long): RetrofitClient {
        //Todo:cacheStorageSpace is Byte
        this.cacheStorageSpace = cacheStorageSpace
        return setupCaching(context)
    }

    fun setCacheLifeTime(cacheLifeTime: Long): RetrofitClient {
        //Todo:cacheTimeLimit is Millisecond
        this.cacheTimeLimit = cacheLifeTime
        return this
    }

    fun setCacheLifeTimeDays(cacheLifeTimeDays: Int): RetrofitClient {
        return setCacheLifeTime(TimeUnit.DAYS.toMillis(cacheLifeTimeDays.toLong()))
    }

    protected fun setupCaching(context: Context): RetrofitClient {
        this.caching = true
        myCache = Cache(context.cacheDir, cacheStorageSpace)
        return this
    }

    //Build
    fun build(context: Context? = null): RetrofitClient {
        return build(BASE_URL, context)
    }

    fun build(urlKey: String, context: Context? = null): RetrofitClient {
        val okHttpClientBuilder = OkHttpClient.Builder()

        if (logRequest) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(interceptor)
        }

        certificate?.let {
            try {
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStore.load(null, null)

                val bis = BufferedInputStream(it)
                val certificateFactory = CertificateFactory.getInstance("X.509")

                while (bis.available() > 0) {
                    val cert = certificateFactory.generateCertificate(bis)
                    keyStore.setCertificateEntry(urls[urlKey], cert)
                }

                val trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                trustManagerFactory.init(keyStore)
                val trustManagers = trustManagerFactory.trustManagers
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustManagers, null)

                okHttpClientBuilder.sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
            } catch (e: Exception) {
                Log.e("RetrofitHelper", "RetrofitClient::build certificate Error -> ", e)
            }
        }

        myCache?.let {
            okHttpClientBuilder.cache(it)
        }

        val okHttpClient = okHttpClientBuilder.connectTimeout(connectionTimeout, TimeUnit.SECONDS) // connect timeout
            .readTimeout(readingTimeout, TimeUnit.SECONDS)
            .addInterceptor {
                var request = it.request()

                request = if (!caching || (context != null && hasNetworkConnection(context))) {
                    /*
                    *  If there is Internet, get the cache that was stored 5 seconds ago.
                    *  If the cache is older than 5 seconds, then discard it,
                    *  and indicate an error in fetching the response.
                    *  The 'max-age' attribute is responsible for this behavior.
                    */
                    if (logRequest && caching)
                        Log.d("OkHttp", "OnlineMode")
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                } else {
                    /*
                    *  If there is no Internet, get the cache that was stored $cacheTimeLimit ago.
                    *  If the cache is older than $cacheTimeLimit, then discard it,
                    *  and indicate an error in fetching the response.
                    *  The 'max-stale' attribute is responsible for this behavior.
                    *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
                    */
                    if (logRequest)
                        Log.d("OkHttp", "OfflineMode")
                    request.newBuilder().header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=$cacheTimeLimit"
                    ).build()
                }

                val nb = request.newBuilder()
                headers.forEach { header ->
                    nb.addHeader(header.key, header.value)
                }
                it.proceed(nb.build())
            }
            .build()

        val gSonBuilder = GsonBuilder()
            .setLenient()
            .create()

        if (urls[urlKey].isNullOrBlank())
            throw NullPointerException("RetrofitHelper::RetrofitClient::build -> baseUrl or urlKey is null")

        retrofits[urlKey] = Retrofit.Builder()
            .baseUrl(urls[urlKey]!!)
            .addConverterFactory(GsonConverterFactory.create(gSonBuilder))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        return this
    }

    //getClient
    fun getClient(context: Context? = null): Retrofit {
        return getClient(BASE_URL, context)
    }

    fun getClient(urlKey: String, context: Context? = null): Retrofit {
        if (retrofits[urlKey] == null) {
            build(urlKey, context)
        }
        return retrofits[urlKey]!!
    }

    inner class Get<myResponse> : BaseRequest<myResponse>() {
        override fun run(context: Context?) {
            super.run(context)
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!, context)
            requestHandler!!.onBeforeSend()
            (retrofit.create(ApiInterface::class.java).get(
                path!!,
                urlParams
            ) as Observable<retrofit2.Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(classOfT!!, requestHandler) {
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    inner class Post<myRequest, myResponse> : PostBaseRequest<myRequest, myResponse>() {
        override fun run(context: Context?) {
            super.run(context)

            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!, context)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).post(
                path!!,
                urlParams,
                request
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(classOfT!!, requestHandler) {
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    inner class Put<myRequest, myResponse> : PostBaseRequest<myRequest, myResponse>() {
        override fun run(context: Context?) {
            super.run(context)
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!, context)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).put(
                path!!,
                urlParams,
                request
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(classOfT!!, requestHandler) {
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    inner class Path<myRequest, myResponse> : PostBaseRequest<myRequest, myResponse>() {
        override fun run(context: Context?) {
            super.run(context)
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!, context)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).path(
                path!!,
                urlParams,
                request
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(classOfT!!, requestHandler) {
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    inner class Delete<myResponse> : BaseRequest<myResponse>() {
        override fun run(context: Context?) {
            super.run(context)
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!, context)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).delete(
                path!!,
                urlParams
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(classOfT!!, requestHandler) {
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    inner class Option<myRequest, myResponse> : PostBaseRequest<myRequest, myResponse>() {
        override fun run(context: Context?) {
            super.run(context)
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!, context)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).option(
                path!!,
                urlParams,
                request
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(classOfT!!, requestHandler) {
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    inner class MultiPart<myResponse> : MultiPartBaseRequest<myResponse>() {
        override fun run(context: Context?) {
            super.run(context)
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!, context)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).multiPart(
                path!!,
                urlParams,
                part!!
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(classOfT!!, requestHandler) {
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    abstract inner class BaseRequest<myResponse> {
        var requestHeader: HashMap<String, String> = HashMap()
        var baseUrlKey: String? = null
            get() {
                if (field != null)
                    return field
                return BASE_URL
            }
        var path: String? = null
        var urlParams: HashMap<String, String> = HashMap()
        var classOfT: Class<myResponse>? = null
        var requestHandler: RequestHandler<myResponse>? = null
        var context: Context? = null

        open fun setRequestHeader(key: String, value: String): BaseRequest<myResponse> {
            this.requestHeader[key] = value
            return this
        }

        open fun setRequestHeader(requestHeader: HashMap<String, String>): BaseRequest<myResponse> {
            this.requestHeader.putAll(requestHeader)
            return this
        }

        open fun clearRequestHeader(): BaseRequest<myResponse> {
            this.requestHeader.clear()
            return this
        }

        open fun setBaseUrlKey(baseUrlKey: String): BaseRequest<myResponse> {
            this.baseUrlKey = baseUrlKey
            return this
        }

        open fun setPath(path: String): BaseRequest<myResponse> {
            this.path = path
            return this
        }

        open fun setUrlParams(key: String, value: String): BaseRequest<myResponse> {
            this.urlParams[key] = value
            return this
        }

        open fun setUrlParams(urlParams: HashMap<String, String>): BaseRequest<myResponse> {
            this.urlParams.putAll(urlParams)
            return this
        }

        open fun clearUrlParams(): BaseRequest<myResponse> {
            this.urlParams.clear()
            return this
        }

        open fun setRequestHandler(
            classOfT: Class<myResponse>,
            requestHandler: RequestHandler<myResponse>
        ): BaseRequest<myResponse> {
            this.classOfT = classOfT
            this.requestHandler = requestHandler
            return this
        }

        open fun run(context: Context? = null) {
            this.context = context
        }
    }

    abstract inner class PostBaseRequest<myRequest, myResponse> : BaseRequest<myResponse>() {
        var request: myRequest? = null

        open fun setRequest(request: myRequest): PostBaseRequest<myRequest, myResponse> {
            this.request = request
            return this
        }

        override fun setRequestHeader(key: String, value: String): PostBaseRequest<myRequest, myResponse> {
            super.setRequestHeader(key, value)
            return this
        }

        override fun setRequestHeader(requestHeader: HashMap<String, String>): PostBaseRequest<myRequest, myResponse> {
            super.setRequestHeader(requestHeader)
            return this
        }

        override fun clearRequestHeader(): PostBaseRequest<myRequest, myResponse> {
            super.clearRequestHeader()
            return this
        }

        override fun setBaseUrlKey(baseUrlKey: String): PostBaseRequest<myRequest, myResponse> {
            super.setBaseUrlKey(baseUrlKey)
            return this
        }

        override fun setPath(path: String): PostBaseRequest<myRequest, myResponse> {
            super.setPath(path)
            return this
        }

        override fun setUrlParams(key: String, value: String): PostBaseRequest<myRequest, myResponse> {
            super.setUrlParams(key, value)
            return this
        }

        override fun setUrlParams(urlParams: HashMap<String, String>): PostBaseRequest<myRequest, myResponse> {
            super.setUrlParams(urlParams)
            return this
        }

        override fun clearUrlParams(): PostBaseRequest<myRequest, myResponse> {
            super.clearUrlParams()
            return this
        }

        override fun setRequestHandler(
            classOfT: Class<myResponse>,
            requestHandler: RequestHandler<myResponse>
        ): PostBaseRequest<myRequest, myResponse> {
            super.setRequestHandler(classOfT, requestHandler)
            return this
        }
    }

    abstract inner class MultiPartBaseRequest<myResponse> {
        var requestHeader: HashMap<String, String> = HashMap()
        var baseUrlKey: String? = null
        var path: String? = null
        var urlParams: HashMap<String, String> = HashMap()
        var part: MultipartBody.Part? = null
        var classOfT: Class<myResponse>? = null
        var requestHandler: RequestHandler<myResponse>? = null
        var context: Context? = null
        open fun setRequestHeader(key: String, value: String): MultiPartBaseRequest<myResponse> {
            this.requestHeader[key] = value
            return this
        }

        open fun setRequestHeader(requestHeader: HashMap<String, String>): MultiPartBaseRequest<myResponse> {
            this.requestHeader.putAll(requestHeader)
            return this
        }

        open fun clearRequestHeader(): MultiPartBaseRequest<myResponse> {
            this.requestHeader.clear()
            return this
        }

        open fun setBaseUrlKey(baseUrlKey: String): MultiPartBaseRequest<myResponse> {
            this.baseUrlKey = baseUrlKey
            return this
        }

        open fun setPath(path: String): MultiPartBaseRequest<myResponse> {
            this.path = path
            return this
        }

        open fun setUrlParams(key: String, value: String): MultiPartBaseRequest<myResponse> {
            this.urlParams[key] = value
            return this
        }

        open fun setUrlParams(urlParams: HashMap<String, String>): MultiPartBaseRequest<myResponse> {
            this.urlParams.putAll(urlParams)
            return this
        }

        open fun clearUrlParams(): MultiPartBaseRequest<myResponse> {
            this.urlParams.clear()
            return this
        }

        open fun setPart(part: MultipartBody.Part): MultiPartBaseRequest<myResponse> {
            this.part = part
            return this
        }

        open fun setPart(part: File, name: String): MultiPartBaseRequest<myResponse> {
            return setPart(FileUtils.fileToPart(part, name))
        }

        open fun setPart(activity: Activity, bitmap: Bitmap, name: String): MultiPartBaseRequest<myResponse> {
            return setPart(FileUtils.bitmapToPart(activity, bitmap, name))
        }

        open fun setRequestHandler(
            classOfT: Class<myResponse>,
            requestHandler: RequestHandler<myResponse>
        ): MultiPartBaseRequest<myResponse> {
            this.classOfT = classOfT
            this.requestHandler = requestHandler
            return this
        }

        open fun run(context: Context?) {
            this.context = context
        }
    }

    open class MySubscriber<T>(private val classOfT: Class<T>, private val requestHandler: RequestHandler<T>?) :
        Subscriber<Response<T>>() {
        override fun onNext(t: Response<T>?) {
//            if (t != null) {
//                Log.e("Request", "code -> ${t.code()}")
//                Log.e("Request", "header -> ${t.headers()}")
//                Log.e("Request", "body -> ${t.body()}")
//            }
            if (t == null) {
                requestHandler?.onFailed(Throwable("Response in null"))
                return
            }

            val res = Response(t, classOfT)

            if (t.code() != 200) {
                requestHandler?.onError(res)
                return
            }
            requestHandler?.onSuccess(res)
        }

        override fun onCompleted() {
        }

        override fun onError(e: Throwable?) {
            e?.printStackTrace()
            requestHandler?.onFailed(e)

        }
    }

    //Utils
    fun hasNetworkConnection(context: Context): Boolean {
        var isConnected = false // Initial Value
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }
}

