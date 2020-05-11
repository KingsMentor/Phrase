package com.example.phrase

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.helpers.PhraseSpannableStringBuilder
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.BehaviourOptions
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.translateMedium.medium.GoogleTranslate


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val font = Typeface.createFromAsset(assets, "rb.ttf")
        Phrase.with(GoogleTranslate(this, R.raw.credential))
            .options(
                PhraseOptions.options("fr")
                    .excludeSources(listOf("es"))
                    .behaviourOptions(
                        BehaviourOptions.options()
                            .signatureColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                            .signatureTypeFace(font).build()
                    )
                    .build("Translate") { phraseTranslation ->
                        "Translated from ${phraseTranslation.source?.name} by"
                    }
            )
            .setUp()


        translated_txt.prepare(source_edit.text.toString(), null, object : PhraseTranslateListener {
            override fun onPhraseTranslating() {
            }

            override fun onPhraseTranslated(phraseTranslation: PhraseTranslation?) {
                detected_language.text =
                    ("${phraseTranslation?.source?.name} (${phraseTranslation?.source?.code})")
            }
        })
        update_source_btn.setOnClickListener {
            translated_txt.updateSource(source_edit.text.toString())
        }

        textView.setOnClickListener {
            Log.e("text", translated_txt.text.toString())
        }

        FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT

        source_edit.text.clear()

    }

}