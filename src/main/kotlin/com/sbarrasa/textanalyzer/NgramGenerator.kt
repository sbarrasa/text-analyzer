package com.sbarrasa.textanalyzer

interface NgramGenerator {
    fun generate(tokens: List<String>, nRange: IntRange = 1..2): List<String>
}
