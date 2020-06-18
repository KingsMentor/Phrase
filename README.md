# Introducing Phrase

Phrase provides a way to translate user generated content to a desired target language using different translation engines or options of your choice.

Phrase was built to remove the constraint of having to use one translation engine in your application. It helps android developers leverage the strength of different translation engines while giving the best experience to Users. 

This library was inspired by how twitter handles in-app content translation. 

[ ![Download](https://api.bintray.com/packages/kingsmentor/maven/phrase/images/download.svg) ](https://bintray.com/kingsmentor/maven/phrase/_latestVersion)


![Lib Sample](https://github.com/KingsMentor/phrase/blob/master/phrase.gif)

# Getting Started 

Add this to dependencies in apps `build.gradle`

```
implementation 'xyz.belvi.translate:phrase:1.0.0'

```

## Setting Up Phrase

A single instance of phrase is instantiated for the lifetime of the application. This instance can be created in the application call, activity or any implementation that best suites you. Here's an example showing how to set up the library with some basic options. 

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

Let's talk about the parameters required in setting up phrase: 

### `mediums`

With mediums, you specify a list of `TranslationMedium` to use in order of fallbacks. Phrase currently supports: 

* `GoogleTranslate` -  Translation medium using Google Translate Engine
* `FirebaseMLKitTranslate` - Translation Medium Using Google Translate Engine through FirebaseML Kit.
* `DeepL` - Translation medium using DeepL Translation Engine

Phrase also allows implementation of custom translation medium if any of the inbuilt translation mediums doesn't meet the requirements you have in mind. 

#### Building Custom TranslationMedium

Adding a Custom TranslationMedium involves extending `TranslationMedium` and implementing the required members.

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



### `options`

Options provide a way to define translation preferences and library behaivour. 
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

#### Understanding and Building Phrase Options.

1. `targeting` - Set a target language for translation. When not provided, Phrase uses `Locale.getDefault().language` to get device default language.  For Language code,  you can find [this list](https://cloud.google.com/translate/docs/languages) helpful. Phrase also provides:
```kotlin
    enum class Languages(val code: String)
```  
You can use this in selecting a Target Language.  
```kotlin
options {
    targeting = Languages.French.code
}
```

>Phrase uses the target language to:
>- Know which language to translate content to. 
>- Know when to show the `translate` option to the user. Translation action only shows when the detected language of the source is not the same as the targeted language. This also means that there's no translation query executed when source and target language is the same. 

2. `actionLabel` - action label defines the text users see which prompts for translation. This is only visible when the source text is in a language different from the targeted language. This can be hidden by passing `BEHAVIOR_HIDE_TRANSLATE_PROMPT` in `behaviorFlags`.

```kotlin
options {
    actionlabel = "Translate"
}
```
![actionlabel sample](https://github.com/KingsMentor/Phrase/blob/master/imgs/actionlabel.png)

3. `resultActionLabel` - this defines the actiontext users see when a content has been translated. In the sample application, it gives credit to the translation engine used but this can also be customised. Credit can be hidden by passing `BEHAVIOR_HIDE_CREDIT_SIGNATURE` in `behaviorFlags`.
```kotlin
options{
    resultActionLabel = { phraseTranslation ->
        //
        "Translated with "
    }
}
```

![actionlabel sample](https://github.com/KingsMentor/Phrase/blob/master/imgs/resultActionLabelImg.png)

### Understanding Phrase Models and Listener 

##### PhraseDection

##### PhraseTranslation

##### PhraseTranslateListener
