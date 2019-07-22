# RetrofitHelper Add
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

dependencies {
	        implementation 'com.github.G-Corporation:RetrofitHelper:1.1.5'
	}

# installation

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




