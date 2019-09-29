package com.gcorp.retrofithelperexample

import android.app.Application
import com.gcorp.retrofithelper.RetrofitClient
import java.util.*

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        retrofitClient =
            RetrofitClient.instance
                    //api url
                .setBaseUrl("https://reqres.in/")
                    //you can set multiple urls
//                .setUrl("example","http://ngrok.io/api/")
                    //set timeouts
                .setConnectionTimeout(4)
                .setReadingTimeout(15)
                    //enable cache
                .enableCaching(this)
                    //add Headers
                .addHeader("Content-Type", "application/json")
                .addHeader("client", "android")
                .addHeader("language", Locale.getDefault().language)
                .addHeader("os", android.os.Build.VERSION.RELEASE)

    }

    companion object {

        lateinit var retrofitClient: RetrofitClient

    }
}