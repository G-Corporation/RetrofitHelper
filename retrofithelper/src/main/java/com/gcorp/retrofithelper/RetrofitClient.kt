package com.gcorp.retrofithelper

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
import java.lang.NullPointerException
import java.net.URL
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class RetrofitClient {
    companion object {
        private val BASE_URL = "RETROFIT_CLIENT_BASE_URL"
        private fun setMap(key: String, value: String): HashMap<String, String> {
            val map = HashMap<String, String>()
            map[key] = value
            return map
        }
    }

    private val urls = HashMap<String, String>()
    private var logRequest: Boolean = true
    private var connectionTimeout: Long = 15
    private var readingTimeout: Long = 15
    private var certificate: InputStream? = null
    private var headers = HashMap<String, String>()
    private val retrofits = HashMap<String, Retrofit>()

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
            if (this.headers[it.key] != null){
                Log.e("myApp","remove ${it.key} -> ${it.value}")
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

    //Build
    fun build(): RetrofitClient {
        return build(BASE_URL)
    }

    fun build(urlKey: String): RetrofitClient {
        val okHttpClientBuilder = OkHttpClient.Builder()

        if (logRequest) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(interceptor)
        }

        if (certificate != null) {
            try {
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStore.load(null, null)

                val bis = BufferedInputStream(certificate)
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

        val okHttpClient = okHttpClientBuilder.connectTimeout(connectionTimeout, TimeUnit.SECONDS) // connect timeout
            .readTimeout(readingTimeout, TimeUnit.SECONDS)
            .addInterceptor {
                val nb = it.request().newBuilder()
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
    fun getClient(): Retrofit {
        return getClient(BASE_URL)
    }

    fun getClient(urlKey: String): Retrofit {
        if (retrofits[urlKey] == null) {
            build(urlKey)
        }
        return retrofits[urlKey]!!
    }

    inner class Get<myResponse> : BaseRequest<myResponse>() {
        override fun run() {
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!)
            requestHandler!!.onBeforeSend()
            (retrofit.create(ApiInterface::class.java).get(
                path!!,
                urlParams
            ) as Observable<retrofit2.Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(requestHandler){
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    inner class Post<myRequest, myResponse> : PostBaseRequest<myRequest, myResponse>() {
        override fun run() {
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).post(
                path!!,
                urlParams,
                request
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(requestHandler){
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }
    
    inner class Put<myRequest, myResponse> : PostBaseRequest<myRequest, myResponse>() {
        override fun run() {
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).put(
                path!!,
                urlParams,
                request
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(requestHandler){
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    inner class Path<myRequest, myResponse> : PostBaseRequest<myRequest, myResponse>() {
        override fun run() {
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).path(
                path!!,
                urlParams,
                request
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(requestHandler){
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    inner class Delete<myResponse> : BaseRequest<myResponse>() {
        override fun run() {
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).delete(
                path!!,
                urlParams
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(requestHandler){
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    inner class Option<myRequest, myResponse> : PostBaseRequest<myRequest, myResponse>() {
        override fun run() {
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).option(
                path!!,
                urlParams,
                request
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(requestHandler){
                    override fun onCompleted() {
                        super.onCompleted()
                        removeHeader(requestHeader)
                    }
                })
        }
    }

    inner class MultiPart<myResponse> : MultiPartBaseRequest<myResponse>() {
        override fun run() {
            val retrofit = addHeader(requestHeader).getClient(baseUrlKey!!)

            requestHandler!!.onBeforeSend()

            (retrofit.create(ApiInterface::class.java).multiPart(
                path!!,
                urlParams,
                part!!
            ) as Observable<Response<myResponse>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySubscriber<myResponse>(requestHandler){
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
        var path: String? = null
        var urlParams: HashMap<String, String> = HashMap()
        var requestHandler: RequestHandler<myResponse>? = null

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

        open fun setRequestHandler(requestHandler: RequestHandler<myResponse>): BaseRequest<myResponse> {
            this.requestHandler = requestHandler
            return this
        }

        open fun run() {
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

        override fun setRequestHandler(requestHandler: RequestHandler<myResponse>): PostBaseRequest<myRequest, myResponse> {
            super.setRequestHandler(requestHandler)
            return this
        }
    }

    abstract inner class MultiPartBaseRequest<myResponse> {
        var requestHeader: HashMap<String, String> = HashMap()
        var baseUrlKey: String? = null
        var path: String? = null
        var urlParams: HashMap<String, String> = HashMap()
        var part: MultipartBody.Part? = null
        var requestHandler: RequestHandler<myResponse>? = null
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

        open fun setRequestHandler(requestHandler: RequestHandler<myResponse>): MultiPartBaseRequest<myResponse> {
            this.requestHandler = requestHandler
            return this
        }

        open fun run() {
        }
    }

    open class MySubscriber<T>(private val requestHandler: RequestHandler<T>?):Subscriber<Response<T>>(){
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

}

