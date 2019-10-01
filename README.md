# Retrofit Helper [![](https://jitpack.io/v/G-Corporation/RetrofitHelper.svg)](https://jitpack.io/#G-Corporation/RetrofitHelper)
 
> This is a library written in __Kotlin__, for simplifying api calls with __retrofit__.  

It's as easy as this :
```kotlin
 retrofitClient.Get<GetResponseModel>()
            .setPath("api/users/2"))
            .setResponseHandler(GetResponseModel::class.java,
                object : ResponseHandler<GetResponseModel>() {
                    override fun onSuccess(response: Response<GetResponseModel>) {
                        super.onSuccess(response)
                        //Handle Response
                    }
                }).run(this)
```  

### Documentation can be found in [wiki](https://github.com/G-Corporation/RetrofitHelper/wiki) or just see the [exapmle app](https://github.com/G-Corporation/RetrofitHelper/tree/master/app).  
Thanks to these beautifull libraries : [OkHttp](https://square.github.io/okhttp/), [RxAndroid](https://github.com/ReactiveX/RxAndroid), [Retrofit](https://square.github.io/retrofit/), [Gson](https://github.com/square/retrofit/tree/master/retrofit-converters/gson)
  

## Adding to project  

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' 
			}
		}

	dependencies {
	        implementation 'com.github.G-Corporation:RetrofitHelper:`1.1.6`'
	}
```  

## Application Class  

```kotlin
class Application : Application() {

	override fun onCreate() {
	super.onCreate()

		retrofitClient = RetrofitClient.instance
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
```  

## POST  
Sample `POST` request:  

>For more Api call methods see [API Calls](https://github.com/G-Corporation/RetrofitHelper/wiki/API-calls).  

```kotlin
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
```




### Backers

<a href="https://github.com/Aryan-mor" target="_blank">
	<img src="https://avatars1.githubusercontent.com/u/22844436?s=460&v=4">
</a>
<a href="https://github.com/mojirzg" target="_blank">
	<img src="https://avatars3.githubusercontent.com/u/29694641?s=460&v=4">
</a>

