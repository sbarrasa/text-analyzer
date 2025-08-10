package com.sbarrasa.textanalyzer

interface Tokenizer {
    fun tokenize(text: String): List<String>
}
