package xyz.belvi.phrase.translateMedium.medium.retrofit


import retrofit2.http.POST
import retrofit2.http.Query
import xyz.belvi.phrase.translateMedium.medium.DeepL

interface DeepLApi {

    @POST("/v2/translate")
    suspend fun translate(
        @Query("auth_key") auth_key: String,
        @Query("text") text: String,
        @Query("target_lang") target_lang: String
    ): DeepL.DeepLTranslationResponse


}