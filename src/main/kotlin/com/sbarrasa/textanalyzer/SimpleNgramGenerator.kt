package com.sbarrasa.textanalyzer

class SimpleNgramGenerator : NgramGenerator {
    override fun generate(tokens: List<String>, nRange: IntRange): List<String> {
        if (tokens.isEmpty()) return emptyList()
        val result = ArrayList<String>()
        for (n in nRange) {
            if (n <= 0) continue
            if (n == 1) {
                result.addAll(tokens)
            } else if (tokens.size >= n) {
                for (i in 0 until tokens.size - n + 1) {
                    result.add(tokens.subList(i, i + n).joinToString(" "))
                }
            }
        }
        return result
    }
}
