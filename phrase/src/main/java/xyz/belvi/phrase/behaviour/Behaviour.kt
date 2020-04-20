package xyz.belvi.phrase.behaviour

sealed class Behaviour {
    object AUTO_DETECT : Behaviour()
    object AUTO_TRANSLATE : Behaviour()
    object REPLACE_SOURCE_TEXT : Behaviour()
    object HIDE_CREDIT_MEDIUM : Behaviour()
}