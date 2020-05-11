package xyz.belvi.phrase.options

import android.graphics.Color
import android.graphics.Typeface
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import xyz.belvi.phrase.BehaviourOptionsUseCase
import xyz.belvi.phrase.PhraseImpl

data class BehaviourOptions internal constructor(
    val behaviours: Behaviour = Behaviour(),
    val signatureTypeFace: Typeface? = null,
    @ColorInt val signatureColor: Int = Color.BLACK,
    @AnimRes val switchAnim: Int = 0
) {
    companion object {
        fun options(): BehaviourOptionsUseCase {
            return PhraseImpl.Companion.BehaviourOptionsBuilder()
        }
    }
}