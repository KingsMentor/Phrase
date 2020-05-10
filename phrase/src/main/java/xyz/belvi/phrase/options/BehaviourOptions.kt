package xyz.belvi.phrase.options

import android.graphics.Typeface
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import xyz.belvi.phrase.BehaviourOptionsUseCase
import xyz.belvi.phrase.PhraseImpl

data class BehaviourOptions internal constructor(
    val behaviours: List<Behaviour>,
    val signatureTypeFace: Typeface?,
    @ColorInt val signatureColor: Int?,
    @AnimRes val switchAnim: Int
) {
    companion object {
        fun options(): BehaviourOptionsUseCase {
            return PhraseImpl.Companion.BehaviourOptionsBuilder()
        }
    }
}