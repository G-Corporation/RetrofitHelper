package com.gcorp.retrofithelperexample

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gcorp.retrofithelper.RequestHandler
import com.gcorp.retrofithelper.Response
import com.gcorp.retrofithelperexample.BaseApp.Companion.retrofitClient
import com.google.gson.Gson
import java.io.Serializable
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
//        retrofitClient.Put<T,T2>()
//            .setBaseUrlKey("salam")
//            .setRequestHeader("tcl", "salam2")
//            .setRequestHeader("vcl", "vcl1")
//            .setPath("test")
//            .setUrlParams("param1", "Salam")
//            .setRequest(T())
//            .setRequestHandler(object : RequestHandler<T2>(){
//                override fun onSuccess(response: Response<T2>) {
//                    super.onSuccess(response)
//                    retrofitClient.Put<T,T2>()
//                        .setBaseUrlKey("salam")
//                        .setRequestHeader("vcl2", "vcl2")
//                        .setPath("test")
//                        .setUrlParams("param1", "Salam")
//                        .setRequest(T())
//                        .setRequestHandler(object : RequestHandler<T2>(){
//                            override fun onSuccess(response: Response<T2>) {
//                                super.onSuccess(response)
//
//                            }
//
//                        })
//                        .run()
//                }
//            })
//            .run()


        retrofitClient.Post<T,T3>()
            .setPath("todo/1")
            .setRequestHandler(T3::class.java,object : RequestHandler<T3>() {
                override fun onSuccess(response: Response<T3>) {
                    super.onSuccess(response)

//                    EEEERRRRRRRRRORRRRRRRRRR is hear
//                            ||||||||||||||||||
//                            >>>>>>>>>>>>>>>

                    Log.e("Ary", "raw -> " + response.raw!!.toString())
                    Log.e("Ary", "raw.body -> " + response.raw!!.body())

                    Log.e("Ary", "body -> " + response.body.name)
                }

                override fun onError(response: Response<T3>?) {
                    super.onError(response)
                }

                override fun onFailed(e: Throwable?) {
                    super.onFailed(e)
                    Log.e("Error", ":DDD errrrror -> ${e!!.message}")
                }
            })
            .run()
    }

    inner class T {
        var bye: String = ":DDDDDDD"
    }

    open inner class T2 {
        var id: String = "pashm"
        var name: String = "pashm"
        var isComplete: Boolean = false
    }
}
