package com.sbarrasa.textanalyzer

interface Preprocessor {
   fun normalize(text: String): String
}