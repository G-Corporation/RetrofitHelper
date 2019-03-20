package com.gcorp.retrofithelper

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import rx.Observable


interface ApiInterface {
    @GET
    fun get(@Url url: String, @QueryMap urlParams: Any?): Observable<Response<Any?>>

    @POST
    fun post(@Url url: String, @QueryMap urlParams: HashMap<String, String>?, @Body request: Any?): Observable<Response<Any?>>

    @PUT
    fun put(@Url url: String, @QueryMap urlParams: HashMap<String, String>?, @Body request: Any?): Observable<Response<Any?>>

    @PATCH
    fun path(@Url url: String, @QueryMap urlParams: HashMap<String, String>?, @Body request: Any?): Observable<Response<Any?>>

    @DELETE
    fun delete(@Url url: String, @QueryMap urlParams: HashMap<String, String>?): Observable<Response<Any?>>

    @OPTIONS
    fun option(@Url url: String, @QueryMap urlParams: HashMap<String, String>?, @Body request: Any?): Observable<Response<Any?>>

    @Multipart
    @POST
    fun multiPart(@Url url: String,@QueryMap urlParams: HashMap<String, String>?, @Part part: MultipartBody.Part): Observable<Response<Any?>>
}
