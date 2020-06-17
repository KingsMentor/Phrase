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

With mediums, you specify a list of `TranslationMedium` to use in order of fallbacks. Phrase currently supports: 

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

![actionlabel sample](https://github.com/KingsMentor/Phrase/blob/master/imgs/resultActionLabel.png)

### Understanding Phrase Models and Listener 

##### PhraseDection

##### PhraseTranslation

##### PhraseTranslateListener
