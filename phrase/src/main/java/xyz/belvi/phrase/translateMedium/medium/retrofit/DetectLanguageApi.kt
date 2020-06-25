package xyz.belvi.phrase.translateMedium.medium.retrofit


import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import xyz.belvi.phrase.translateMedium.medium.DetectLanguage

interface DetectLanguageApi {

    @POST("/0.2/detect")
    suspend fun detect(
        @Header("AUTHORIZATION") token: String,
        @Query("q") text: String
    ): DetectLanguage.DetectionResponse


}