package com.sbarrasa.textanalyzer

interface NgramGenerator {
    /**
     * Genera n-gramas para los tamaños especificados (por ejemplo, 1..2 para uni+bi).
     */
    fun generate(tokens: List<String>, nRange: IntRange = 1..2): List<String>
}
