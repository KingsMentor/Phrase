package xyz.belvi.phrase.options

import xyz.belvi.phrase.translateMedium.TranslationMedium

data class PhraseDetected(
    val text: String, val code: String, val name: String,
    val detectMedium: TranslationMedium
)

data class PhraseTranslation(
    val translation: String,
    val source: PhraseDetected?,
    val translationMedium: TranslationMedium?
)


class Behaviour {

    companion object{
        @BehaviorInt val REPLACE_SOURCE_TEXT: Int = 1
        @BehaviorInt val BEHAVIOR_TRANSLATE_PREFERRED_SOURCE_ONLY: Int = 2
        @BehaviorInt val BEHAVIOR_SKIP_DETECTION: Int = 3
        @BehaviorInt val BEHAVIOR_HIDE_CREDIT_SIGNATURE: Int = 4
    }


    private val behaviorSet = mutableSetOf<Int>()

    internal fun includeBehavior(behavior: Int){
        behaviorSet.add(behavior)
    }

    internal fun hasBehavior(behavior: Int):Boolean{
        return behaviorSet.contains(behavior)
    }

    internal fun replaceSourceText() =  behaviorSet.contains(REPLACE_SOURCE_TEXT)

    internal fun skipDetection() =  behaviorSet.contains(BEHAVIOR_SKIP_DETECTION)

    internal fun translatePreferredSourceOnly() =  behaviorSet.contains(BEHAVIOR_TRANSLATE_PREFERRED_SOURCE_ONLY)

    internal fun hideSignature() =  behaviorSet.contains(BEHAVIOR_HIDE_CREDIT_SIGNATURE)



}


@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE
)
annotation class BehaviorInt

