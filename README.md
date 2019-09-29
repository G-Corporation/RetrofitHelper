# Retrofit Helper
This is a library written in __Kotlin__, for simplifying api calls using __retrofit__.  
Documentation can be found in [wiki](https://github.com/G-Corporation/RetrofitHelper/wiki)

## Adding to project
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.G-Corporation:RetrofitHelper:1.1.5'
	}

## Application Class

	class App : Application() {

		  override fun onCreate() {
			super.onCreate()

		       retrofitClient =
			    RetrofitClient.instance
				.setBaseUrl("http://192.168.0.95/")
				.setConnectionTimeout(4)
				.enableCaching(this)
				.setReadingTimeout(15)
				.addHeader("Accept", "application/json")
				.addHeader("client", "android")


		    }

		    companion object {
			lateinit var retrofitClient: RetrofitClient

		    }
	}




