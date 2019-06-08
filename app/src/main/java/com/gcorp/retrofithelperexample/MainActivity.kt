package com.gcorp.retrofithelperexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gcorp.retrofithelper.RequestHandler
import com.gcorp.retrofithelper.Response
import com.gcorp.retrofithelperexample.BaseApp.Companion.retrofitClient
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var text = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
//        retrofitClient.Put<T,T2>()
//            .setBaseUrlKey("salam")
//            .addRequestHeader("tcl", "salam2")
//            .addRequestHeader("vcl", "vcl1")
//            .setPath("test")
//            .setUrlParams("param1", "Salam")
//            .setRequest(T())
//            .setRequestHandler(object : RequestHandler<T2>(){
//                override fun onSuccess(response: Response<T2>) {
//                    super.onSuccess(response)
//                    retrofitClient.Put<T,T2>()
//                        .setBaseUrlKey("salam")
//                        .addRequestHeader("vcl2", "vcl2")
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
        getRequest()
        requestBtn.setOnClickListener {
            getRequest()
        }
    }

    fun getRequest() {
        retrofitClient.Get<T3>()
            .setPath("items.json")
            .addRequestHeader("requestHeaderKey","requestHeaderValue")
            .addRequestHeader("requestHeaderKey2","requestHeaderValue2")
            .setRequestHandler(T3::class.java, object : RequestHandler<T3>() {
                override fun onSuccess(response: Response<T3>) {
                    super.onSuccess(response)
                    Log.e("Log", "raw -> " + response.raw!!.toString())
                    Log.e("Log", "raw.body -> " + response.raw!!.body())

                    text += "\n\n"
                    response.body.items?.forEach {
                        text += it.name + ", "
                    }
                    textTv.text = text
                }

                override fun onError(response: Response<T3>?) {
                    super.onError(response)
                    Log.e("Error", ":DDD errrrror -> onError")
                }

                override fun onFailed(e: Throwable?) {
                    super.onFailed(e)
                    Log.e("Error", ":DDD errrrror -> ${e!!.message}")
                }

                override fun onComplete() {
                    super.onComplete()

                    Log.e("Ari","onComplete :DDDDDDDDDDDDDDDDDD")
                }
            })
            .run(this)
    }

    fun getRequestOnAnotherPath() {
        //path2 is key | value set on BaseApp
        retrofitClient.Get<T3>()
            .setBaseUrlKey("path2")
            .addRequestHeader("requestHeaderKey","requestHeaderValue")
            .setPath("items.json")
            .setRequestHandler(T3::class.java, object : RequestHandler<T3>() {
                override fun onSuccess(response: Response<T3>) {
                    super.onSuccess(response)
                    Log.e("Log", "raw -> " + response.raw!!.toString())
                    Log.e("Log", "raw.body -> " + response.raw!!.body())

                    text += "\n\n"
                    response.body.items?.forEach {
                        text += it.name + ", "
                    }
                    textTv.text = text
                }

                override fun onError(response: Response<T3>?) {
                    super.onError(response)
                    Log.e("Error", ":DDD errrrror -> onError")
                }

                override fun onFailed(e: Throwable?) {
                    super.onFailed(e)
                    Log.e("Error", ":DDD errrrror -> ${e!!.message}")
                }

                override fun onComplete() {
                    super.onComplete()

                    Log.e("Ari","onComplete :DDDDDDDDDDDDDDDDDD")
                }
            })
            .run(this)
    }

    fun postRequest(){
        retrofitClient.Post<T2,T3>()
            .setPath("post-method")
            .addRequestHeader("requestHeaderKey","requestHeaderValue")
            .addRequestHeader("requestHeaderKey2","requestHeaderValue2")
            .setRequestHandler(T3::class.java,object :RequestHandler<T3>(){
                override fun onBeforeSend() {
                    super.onBeforeSend()
                }

                override fun onSuccess(response: Response<T3>) {
                    super.onSuccess(response)
                }

                override fun onError(response: Response<T3>?) {
                    super.onError(response)
                }

                override fun onFailed(e: Throwable?) {
                    super.onFailed(e)
                }

                override fun onComplete() {
                    super.onComplete()
                }
            })
            .run(this)
    }

    fun postRequestOnAnotherPath(){
        retrofitClient.Post<T2,T3>()
            .setBaseUrlKey("path2")
            .addRequestHeader("requestHeaderKey","requestHeaderValue")
            .setPath("post-method")
            .setRequestHandler(T3::class.java,object :RequestHandler<T3>(){
                override fun onBeforeSend() {
                    super.onBeforeSend()
                }

                override fun onSuccess(response: Response<T3>) {
                    super.onSuccess(response)
                }

                override fun onError(response: Response<T3>?) {
                    super.onError(response)
                }

                override fun onFailed(e: Throwable?) {
                    super.onFailed(e)
                }

                override fun onComplete() {
                    super.onComplete()
                }
            })
    }

    inner class T3 {
        val items: List<Item>? = null

        inner class Item{
            var name: String = ""
            var url: String = ""
        }
    }

    open inner class T2 {
        var id: String = "pashm"
        var name: String = "pashm"
        var isComplete: Boolean = false
    }
}
