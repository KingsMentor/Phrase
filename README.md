# Introducing Phrase

Phrase provides a way to translate user generated content to desired target language using different translation engine or options of your choice.

I built phrase to remove the constraint of having to use one translation engine in your application. It helps android developers leverage on the strength of different translation engine while giving the best experience to Users. 

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

### Markdown

Markdown is a lightweight and easy-to-use syntax for styling your writing. It includes conventions for

```markdown
Syntax highlighted code block

# Header 1
## Header 2
### Header 3

- Bulleted
- List

1. Numbered
2. List

**Bold** and _Italic_ and `Code` text

[Link](url) and ![Image](src)
```

For more details see [GitHub Flavored Markdown](https://guides.github.com/features/mastering-markdown/).

### Jekyll Themes

Your Pages site will use the layout and styles from the Jekyll theme you have selected in your [repository settings](https://github.com/KingsMentor/Phrase/settings). The name of this theme is saved in the Jekyll `_config.yml` configuration file.

### Support or Contact

Having trouble with Pages? Check out our [documentation](https://help.github.com/categories/github-pages-basics/) or [contact support](https://github.com/contact) and we’ll help you sort it out.
