package com.sbarrasa.textanalyzer

class SimpleTokenizer(
    private val normalizer: Normalizer = BasicNormalizer()
) : Tokenizer {

    private val whitespaceRegex = "\\s+".toRegex()

    override fun tokenize(text: String): List<String> {
        if (text.isEmpty()) return emptyList()
        val norm = normalizer.normalize(text)
        if (norm.isEmpty()) return emptyList()
        return norm.split(whitespaceRegex).filter { it.isNotBlank() }
    }
}
