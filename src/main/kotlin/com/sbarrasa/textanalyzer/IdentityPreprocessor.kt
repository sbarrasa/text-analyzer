package com.sbarrasa.textanalyzer

class IdentityPreprocessor : Preprocessor {
   override fun normalize(text: String): String = text
}