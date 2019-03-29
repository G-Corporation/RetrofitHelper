package com.gcorp.retrofithelperexample
import android.app.Application
import com.gcorp.retrofithelper.RetrofitClient
import java.util.*

class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        retrofitClient =
            RetrofitClient(this).setBaseUrl("http://192.168.1.2/test/")
                .setUrl("mili","http://7468e347.ngrok.io/api/")
                .setConnectionTimeout(15)
                .enableCaching()
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