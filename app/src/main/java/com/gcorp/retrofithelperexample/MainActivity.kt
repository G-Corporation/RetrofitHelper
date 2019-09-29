package com.gcorp.retrofithelperexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.gcorp.retrofithelper.ResponseHandler
import com.gcorp.retrofithelper.Response
import com.gcorp.retrofithelperexample.Application.Companion.retrofitClient
import com.gcorp.retrofithelperexample.Model.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.MultipartBody
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        send.setOnClickListener {
            when (spinner.selectedItemPosition) {
                // POST
                0 -> postRequest()
                // GET
                1 -> getRequest()
                //PUT
                2 -> putRequest()
                //PATCH
                3 -> patchRequest()
                //DELETE
                4 -> deleteRequest()
            }
        }
    }


    /**
     *  Post Request
     *
     * PostRequestModel for Request body
     * PostResponseModel for Response body
     *
     * NOTE: These methods are for all type of request methods and nor just POST
     *
     * setBody() : sets the request body
     * setUrlParams() : sets the URL Parameters Key-Value, HashMap
     * setBaseUrlKey("key") for key-value urls that had been set in Application class
     * setBody() : sets the request body
     * setBody() : sets the request body
     *
     * ResponseHandler has 6 overrides:
     *  onBeforeSend():
     *      runs before making a request, it's good for showing loading and etc.
     *  onSuccess():
     *      runs when response code is 2**.
     *  onError():
     *      runs when response code is NOT 2**, handle server errors here.
     *  onComplete():
     *      runs after request even when it fails, it's good for stopping loading and etc.
     *  onFailed():
     *      runs on error, either in code or broken network connection
     *
     */
    private fun postRequest() {
        retrofitClient.Post<PostRequestModel, PostResponseModel>()
            .setPath("api/users")
            //set headers Key-Value or HashMap
//            .setRequestHeader()
            //set url params Key-Value or HashMap
//            .setUrlParams()
            .setBody(PostRequestModel("morpheus", "leader"))
            .setResponseHandler(PostResponseModel::class.java,
                object : ResponseHandler<PostResponseModel>() {
                    override fun onSuccess(response: Response<PostResponseModel>) {
                        super.onSuccess(response)
                        //handle response
                        log.text = response.body.toString()
                    }

                    override fun onBeforeSend() {
                        super.onBeforeSend()
                    }

                    override fun onError(response: Response<PostResponseModel>?) {
                        super.onError(response)
                    }

                    override fun onFailed(e: Throwable?) {
                        super.onFailed(e)
                    }

                    override fun onComplete() {
                        super.onComplete()
                    }
                })
            //DO NOT FORGET TO CALL .run()
            .run(this)
    }

    //This is a Sample GET Request
    private fun getRequest() {
        retrofitClient.Get<GetResponseModel>()
            .setPath("api/users/2")
            //set headers Key-Value or HashMap
//            .setRequestHeader()
            //set url params Key-Value or HashMap
//            .setUrlParams()
            .setUrlParams("KEY","Value")
            .setResponseHandler(GetResponseModel::class.java,
                object : ResponseHandler<GetResponseModel>() {
                    override fun onSuccess(response: Response<GetResponseModel>) {
                        super.onSuccess(response)
                        log.text = response.body.toString()
                    }
                }).run(this)
    }

    //This is a Sample PUT Request
    private fun putRequest() {
        retrofitClient.Put<PutRequestModel, PutResponseModel>()
            .setPath("api/users/2")
            //set headers Key-Value or HashMap
//            .setRequestHeader()
            //set url params Key-Value or HashMap
//            .setUrlParams()
            .setBody(PutRequestModel("morpheus","zion resident"))
            .setResponseHandler(PutResponseModel::class.java,object : ResponseHandler<PutResponseModel>(){
                override fun onSuccess(response: Response<PutResponseModel>) {
                    super.onSuccess(response)
                    log.text = response.body.toString()
                }
            })
            .run()

    }

    //This is a Sample PATCH Request
    private fun patchRequest() {
        retrofitClient.Patch<PatchRequestModel, PatchResponseModel>()
            .setPath("api/users/2")
            //set headers Key-Value or HashMap
//            .setRequestHeader()
            //set url params Key-Value or HashMap
//            .setUrlParams()
            .setBody(PatchRequestModel("morpheus","zion resident"))
            .setResponseHandler(PatchResponseModel::class.java,
                object : ResponseHandler<PatchResponseModel>() {
                    override fun onSuccess(response: Response<PatchResponseModel>) {
                        super.onSuccess(response)
                        log.text = response.body.toString()

                    }
                }).run()

    }

    //This is a Sample DELETE Request
    private fun deleteRequest() {
        retrofitClient.Delete<DeleteResponseModel>()
            .setPath("api/users/2")
            //set headers Key-Value or HashMap
//            .setRequestHeader()
            //set url params Key-Value or HashMap
//            .setUrlParams()
            .setResponseHandler(DeleteResponseModel::class.java,
                object : ResponseHandler<DeleteResponseModel>() {
                    override fun onSuccess(response: Response<DeleteResponseModel>) {
                        super.onSuccess(response)
                        log.text = "Deleted Successfully"

                    }
                }).run()
    }

    //This is a sample MULTIPART request
    private fun multiPartRequest(filePath:String){
        //Create a file object using file path
        val file = File(filePath)
        // Create a request body with file and image media type
        val fileReqBody = RequestBody.create(MediaType.parse("image/*"), file)
        // Create MultipartBody.Part using file request-body,file name and part name
        val part = MultipartBody.Part.createFormData("upload", file.name, fileReqBody)
        //Create request body with text description and text media type
        val description = RequestBody.create(MediaType.parse("text/plain"), "image-type")

        retrofitClient.MultiPart<MultiPartResponseModel>()
            .setPath("")
            .setPart(part)
            .setUrlParams("type","image")
            .setRequestHandler(MultiPartResponseModel::class.java,object :ResponseHandler<MultiPartResponseModel>(){
                override fun onSuccess(response: Response<MultiPartResponseModel>) {
                    super.onSuccess(response)
                    //success
                }
            })
    }



}
