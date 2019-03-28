package com.gcorp.retrofithelperexample
import android.app.Application
import com.gcorp.retrofithelper.RetrofitClient
import java.util.*

class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        retrofitClient =
//            RetrofitClient().setBaseUrl("http://192.168.1.2/test/")
            RetrofitClient().setBaseUrl("http://bef0de5a.ngrok.io/api/")
                .setUrl("salam","http://192.168.1.95:81/")
                .setConnectionTimeout(15)
                .setReadingTimeout(15)
                .addHeader("Accept", "application/json")
                .addHeader("client", "android")
                .addHeader("language", Locale.getDefault().language)
                .addHeader("os", android.os.Build.VERSION.RELEASE)
    }

    companion object {
        lateinit var retrofitClient: RetrofitClient
    }
}