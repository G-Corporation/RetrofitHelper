package com.gcorp.retrofithelperexample

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import com.gcorp.retrofithelper.RequestHandler
import com.gcorp.retrofithelper.Response
import com.gcorp.retrofithelperexample.BaseApp.Companion.retrofitClient
import com.google.gson.Gson
import java.io.Serializable
import java.io.StringReader
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

//        val json =
//            "{items=[{name=Fruit Ninja Fight, url=https://www.farsroid.com/wp-content/uploads/Fruit-Ninja-Fight-Logo-150x150.png}, {name=Trailer Park Boys, url=https://www.farsroid.com/wp-content/uploads/Trailer-Park-Boys-Greasy-Money-logo-c-150x150.png}, {name=Cradle of Empires, url=https://www.farsroid.com/wp-content/uploads/Cradle-of-Empires-logo-d-150x150.png}, {name=Disco Ducks, url=https://www.farsroid.com/wp-content/uploads/Disco-Ducks-150x150.png}, {name=Manor Cafe, url=https://www.farsroid.com/wp-content/uploads/Manor-Cafe-logo-f-150x150.png}, {name=Live or Die: Survival, url=https://www.farsroid.com/wp-content/uploads/Live-or-Die-survival-2019-logo-150x150.jpg}]}"
//
//

//        val aa = Gson().fromJson<T3>(Gson().toJson(json), T3::class.java)
//
//        Log.e("Ary", "T3 -> ${aa.items!!.size}")
//        aa.items.forEach {
//            Log.e("Ary", "${it.name} -> ${it.url}")
//        }

        retrofitClient.Get<T2>()
            .setBaseUrlKey("mili")
            .setPath("todo/1")
            .setRequestHandler(T2::class.java, object : RequestHandler<T2>() {
                override fun onSuccess(response: Response<T2>) {
                    super.onSuccess(response)

//                    EEEERRRRRRRRRORRRRRRRRRR is hear
//                            ||||||||||||||||||
//                            >>>>>>>>>>>>>>>

                    Log.e("Ary", "raw -> " + response.raw!!.toString())
                    Log.e("Ary", "raw.body -> " + response.raw!!.body())

                    Log.e("Ary", "body -> " + response.body.name)


                }

                override fun onError(response: Response<T2>?) {
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
