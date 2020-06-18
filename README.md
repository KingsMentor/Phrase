# Introducing Phrase

Phrase provides a way to translate user generated content to desired target language using different translation engine or options of your choice.

I built phrase to remove the constraint of having to use one translation engine in your application. It helps android developers leverage on the strength of different translation engine while giving the best experience to Users. 

This library was inspired by how twitter handles in-app content translation. 

[ ![Download](https://api.bintray.com/packages/kingsmentor/maven/phrase/images/download.svg) ](https://bintray.com/kingsmentor/maven/phrase/_latestVersion)


![Lib Sample](https://github.com/KingsMentor/phrase/blob/master/phrase.gif)

## Getting Started 

Add this to dependency in apps `build.gradle`

```
implementation 'xyz.belvi.translate:phrase:1.0.0'

```

### Setting Up Phrase

A single instance of phrase is instantiated for the lifetime of the application. This instance can be created in the application call , activity or any implementation that best suite you. Here's an example of setting up the library with some basic options. 

```kotlin
phrase {
    mediums = listOf(GoogleTranslate(this@MainActivity, R.raw.credential))
    options {
        targeting = "fr"
        actionLabel = "Translate"
        resultActionLabel = {
            "Translated with "
        }
    }
}

```

Let's talk about the parameters required in setting up phrase : 

#### `mediums`

With mediums, you specify a list of `TranslationMedium` to use in order of fallbacks. When translation fails for the first medium, it fallsback to the next medium on the list.  Phrase currently supports: 

* `GoogleTranslate` -  Translation medium using Google Translate Engine
* `FirebaseMLKitTranslate` - Translation Medium Using Googke Translate Engine through FirebaseML Kit.
* `DeepL` - Translation medium using DeepL Translation Engine

Phrase also allows implementation of custom translation medium of any of the inbuilt translation medium doesn't meet the requirements you have in mind. 

##### Building Custom TranslationMedium

Adding a Custom TranslationMedium involves extending `TranslationMedium` and implementing the required members. Here's an example.

```kotlin
final class DeepL(val apiKey: String) : TranslationMedium() {
    override fun translate(text: String, targeting: String): String {
        TODO("handle text translation here") 
    }

    override fun name(): String {
        TODO("preferred name of translation engine")
    }

    override fun detect(text: String): PhraseDetected {
        TODO("handle language detection here")
    }
}
```
see [GoogleTranslate.kt](https://github.com/KingsMentor/Phrase/blob/master/phrase/src/main/java/xyz/belvi/phrase/translateMedium/medium/GoogleTranslate.kt) for a working example.



#### `options`

Options provide a way to define translation preference and library behaivour. 
Here's an example of how an option is built: 

```kotlin
options {
    targeting = target.text.toString()
    behaviourFlags {
        flags = setOf()
        signatureTypeface = font
        signatureColor =
            ContextCompat.getColor(this@MainActivity, R.color.white)
    }
    actionLabel = "Translate"
    resultActionLabel = {
        detected.text =
            "Detected Language Source: " + it.detectedSource?.languageName ?: ""
        "Translated with "
    }
}
```

##### Understanding and Building Phrase Options.

* `targeting` - Set a target language for translation. When not provided, Phrase uses `Locale.getDefault().language` to get device default language.  For Language code,  you can find [this list](https://cloud.google.com/translate/docs/languages) helpful. Phrase also provides:
```kotlin
    enum class Languages(val code: String)
```
You can use this in selecting a Target Language.  
```kotlin
options {
    targeting = Languages.French.code
}
```

Phrase uses target language in a couple of ways.
1. Know which language to translate content to. 
2. Know when to show user an option to translate. Translation action only shows when the detected language of the source is not same language with the targetted language. This also means that there's no translation query executed when  source and target language is the same. 

* `actionLabel` - action label defines the text users sees that prompts for translation. This only visible when the source text is in another language that is not the targeted language. This can be hidden passing `BEHAVIOR_HIDE_TRANSLATE_PROMPT` in `behaviorFlags`.

```kotlin
options {
    actionlabel = "Translate"
}
```
![actionlabel sample](https://github.com/KingsMentor/Phrase/blob/master/imgs/actionlabel.png)

* `resultActionLabel` - this defines the actiontext the user sees when a content has been translated. In the sample application, It gives credit to the translation engine used but this can also be customised. Credit can be hidden by passing `BEHAVIOR_HIDE_CREDIT_SIGNATURE` in `behaviorFlags`.
```kotlin
options{
    resultActionLabel = { phraseTranslation ->
        //
        "Translated with "
    }
}
```

![actionlabel sample](https://github.com/KingsMentor/Phrase/blob/master/imgs/resultActionLabelImg.png)

* `preferredDetectionMedium` - Phrase alllows you define a preferred medium to use in language detection. You might want to run language detection with another translation engine different from the engine you want to use for translation. This also accept an instance of `TranslationMedium`. If you are using a custom implementation for this, ensure `detect` returns `PhraseDetected`. See [Building Custom TranslationMedium](#building-custom-translationmedium) for further explanation.
```kotlin
options {
    preferredDetectionMedium = GoogleTranslate(this@MainActivity, R.raw.credential)
}
```

* `sourceTranslation` - this option allows you specify a TranslationMedium for a specific source language. This means you can Specify to use `DeepL` to translate any chinese content while Phrase continue to use whatever medium defined via `mediums` to translate other source content. 

Let us dive deeper into what can be acheive using `SourceTranslation`


```kotlin

// MODEL    
data class SourceTranslationOption(
    val sourceLanguageCode: String,
    val targetLanguageCode: List<String> = emptyList(),
    val translate: List<TranslationMedium> = emptyList()
)

//  USAGE
options {
    sourceTranslation =
        listOf(
            SourceTranslationOption(
                Languages.Chinese.code, 
                listOf("fr", "es"),
                listOf(DeepL(""))
            ),
            SourceTranslationOption(
                Languages.German.code,
                listOf("fr", "es", "*"),
                listOf(DeepL(""))
            )
        )
}
```
`sourceTranslationOption.sourceLanguageCode` is the languagecode you want to specify translation medium for
`sourceTranslationOption.targetLanguageCode` is a list of target language to be considered before using this rule. From the example above, DeepL will only be used as a translation medium if the source language is Chinese and the targetted language is either  French or Spanish. 

What then happens when a target language is english in this case ? 
The default translation medium specified in `mediums` will be used. 

Also, From the above example, DeepL will be used as a translation medium if the source language is German and the targetted language is either French or Spanish. The difference between the first and second rule of  `SourceTranslationOption` is that there's `*` included. This means that if Phrase should use DEEPL to for translation as long as the detected source language is German. 

`sourceTranslationOption.translate` - here, you define translation engine preference for the SourceTranslationOption. Order this list based on fallback preferences. 


* `sourcesToExclude` - this option allows you to ignore language of certain sources for translation. This means that of the detected language source is in this list, users wouldn't be shown the translate action. 
```kotlin
options {
    sourcesToExclude = listOf("fr","es","zh")
}
```
This example implies that Phrase wouldn't handle translation for the language sources detected to be in the list provided. 


* `behaviourOptions` - this helps define how Phrase handles UI behaviour. 
```kotlin
options {
    behaviourFlags {
        switchAnim = R.anim.slide_up
        flags = setOf(BEHAVIOR_HIDE_CREDIT_SIGNATURE,BEHAVIOR_TRANSLATE_PREFERRED_SOURCE_ONLY)
        signatureTypeface = typeFace
        signatureColor =
            ContextCompat.getColor(this@MainActivity, R.color.white)
    }
}
```
`switchAnim` -  set anim for text change. If you do not want any text change anomation, do not set this value. 
`signatureTypeface` - Phrase appends Translation engine name to `resultActionLabel`. This is so credit is given to the Translation Engine. `signatureTypeface` defines the typeface for rendering this text. 
`signatureColor` - setting text color for Translation engine credit. 

These behavior can be controlled with flags. In the next phase, I will address flags available in Phrase and how it works. 

### Phrase Behavior Flags
Flags helps define certain behaviour in Phrase to suit your needs. Here's an example of setting flags. 
```kotlin
options {
    behaviourFlags {
        flags = setOf(BEHAVIOR_HIDE_CREDIT_SIGNATURE,BEHAVIOR_TRANSLATE_PREFERRED_SOURCE_ONLY)
    }
}
```
`BEHAVIOR_REPLACE_SOURCE_TEXT` - When this flag is set, the source text is replace with translated text after translation

`BEHAVIOR_TRANSLATE_SOURCE_OPTION_ONLY`-  When this flag is set, Phrase only translate sources included in `SourceTranslationOption` only.

`BEHAVIOR_IGNORE_DETECTION`- setting this flag means you always want to run translation without detection first. For this case, Phrase will always show the actionLabel to the user. 

`BEHAVIOR_HIDE_CREDIT_SIGNATURE` - when this flag is set, Phrase doesn't append credit to resultActionLabel. 

`BEHAVIOR_HIDE_TRANSLATE_PROMPT`- Phrase doesn'y show translate action text when this flag is enabled. 

### Understanding Phrase Models and Listener 

##### PhraseDection

##### PhraseTranslation

##### PhraseTranslateListener
