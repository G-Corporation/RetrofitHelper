package com.gcorp.retrofithelperexample

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gcorp.retrofithelper.RequestHandler
import com.gcorp.retrofithelper.Response
import com.gcorp.retrofithelperexample.BaseApp.Companion.retrofitClient


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        retrofitClient.Put<T,T2>()
            .setBaseUrlKey("salam")
            .setRequestHeader("tcl", "salam2")
            .setRequestHeader("vcl", "vcl1")
            .setPath("test")
            .setUrlParams("param1", "Salam")
            .setRequest(T())
            .setRequestHandler(object : RequestHandler<T2>(){
                override fun onSuccess(response: Response<T2>) {
                    super.onSuccess(response)
                    retrofitClient.Put<T,T2>()
                        .setBaseUrlKey("salam")
                        .setRequestHeader("vcl2", "vcl2")
                        .setPath("test")
                        .setUrlParams("param1", "Salam")
                        .setRequest(T())
                        .setRequestHandler(object : RequestHandler<T2>(){
                            override fun onSuccess(response: Response<T2>) {
                                super.onSuccess(response)

                            }

                        })
                        .run()
                }
            })
            .run()
    }

    inner class T {
        var bye: String = ":DDDDDDD"
    }

    inner class T2 {
        var min_version:String = "pashm"
    }
}
