package com.sbarrasa.textanalyzer


class VocabularyExtractor {
    private val tokenizer = SimpleTokenizer(true)

    fun buildVocabulary(texts: Collection<String>): List<String> {
        val tokenFreq = mutableMapOf<String, Int>()
        
        texts.forEach { text ->
            val tokens = tokenizer.split(text)
            
            tokens.forEach { token ->
                if (token.isNotBlank()) {
                    tokenFreq[token] = tokenFreq.getOrDefault(token, 0) + 1
                }
            }
            
            for (i in 0 until tokens.size - 1) {
                val bigram = "${tokens[i]} ${tokens[i + 1]}"
                if (tokens[i].isNotBlank() && tokens[i + 1].isNotBlank()) {
                    tokenFreq[bigram] = tokenFreq.getOrDefault(bigram, 0) + 1
                }
            }
        }
        
        return tokenFreq.entries
            .sortedByDescending { it.value }
            .take(100)
            .map { it.key }
            .sorted()
    }
}
