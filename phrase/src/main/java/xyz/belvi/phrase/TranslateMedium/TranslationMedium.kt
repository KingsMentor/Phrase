package xyz.belvi.phrase.TranslateMedium

import android.content.Context
import androidx.annotation.RawRes
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.TranslateOptions
import xyz.belvi.phrase.PhraseBuilderUseCase
import java.io.InputStream

abstract class TranslationMedium {

    abstract fun detect()

    abstract fun translate(): String
}