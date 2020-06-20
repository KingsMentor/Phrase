package xyz.belvi.phrase.translateMedium.medium.retrofit

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    fun retrofit(baseUrl: String): Retrofit {
        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder()
                            .setLenient()
                            .create()
                    )
                ) // for serialization. Great resource for json parsing
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // for rx. Enable the use of Observable instead of {@link Call}
                .build()
        }
        return retrofit
    }


}