package xyz.belvi.phrase

import android.widget.TextView
import xyz.belvi.phrase.behaviour.Behaviour
import xyz.belvi.phrase.helpers.PhraseSpannableStringBuilder
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.translateMedium.SourceTranslationOption
import xyz.belvi.phrase.translateMedium.SourceTranslationPreference
import xyz.belvi.phrase.translateMedium.TranslationMedium

internal class PhraseImpl internal constructor() : PhraseUseCase {

    companion object {
        internal lateinit var phrase: Phrase

        class Builder(medium: TranslationMedium) : PhraseBuilderUseCase {
            private var translationMedium = mutableListOf<TranslationMedium>(medium)
            override fun options(phraseOptions: PhraseOptions): PhraseBuilderUseCase {
                phrase.phraseOptions = phraseOptions
                return this
            }

            override fun includeFallback(medium: TranslationMedium): PhraseBuilderUseCase {
                translationMedium.find { it == medium }?.let {
                    translationMedium.remove(medium)
                }
                translationMedium.add(medium)
                return this
            }

            override fun setUp(): Phrase {
                phrase = Phrase()
                return phrase
            }

        }

        class OptionsBuilder :
            PhraseOptionsUseCase {
            private var switchAnim: Int = 0
            private var behaviours = mutableListOf<Behaviour>()
            private lateinit var sourceTranslation: SourceTranslationPreference
            override fun switchAnim(anim: Int): PhraseOptionsUseCase {
                switchAnim = anim
                return this
            }

            override fun includeBehaviours(vararg behaviour: Behaviour): PhraseOptionsUseCase {
                behaviour.forEach {
                    behaviours.add(it)
                }
                return this
            }

            override fun specifySourceTranslation(preferred: SourceTranslationPreference): PhraseOptionsUseCase {
                sourceTranslation = preferred
                return this
            }


            override fun build(): PhraseOptions {
                return PhraseOptions(behaviours, sourceTranslation, switchAnim)
            }
        }

        class SourceOptionsBuilder :
            PhraseSourceTranslationUseCase {
            private var sourceTranslationOptions = mutableListOf<SourceTranslationOption>()
            override fun specifyTranslateOption(
                source: String,
                detect: TranslationMedium,
                translate: TranslationMedium
            ): PhraseSourceTranslationUseCase {
                val index = sourceTranslationOptions.indexOfFirst { it.source == source }
                if (index >= 0)
                    sourceTranslationOptions[index] =
                        SourceTranslationOption(source, detect, translate)
                else
                    sourceTranslationOptions.add(SourceTranslationOption(source, detect, translate))

                return this
            }

            override fun makeOptions(): SourceTranslationPreference {
                return SourceTranslationPreference(sourceTranslationOptions)
            }

        }
    }

    override fun bindTextView(textView: TextView) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun detect(text: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun translate(text: String): PhraseSpannableStringBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun translatePlain(text: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateOptions(phraseOptions: PhraseOptions) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}