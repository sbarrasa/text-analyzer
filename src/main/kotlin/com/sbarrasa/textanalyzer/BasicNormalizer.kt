package com.sbarrasa.textanalyzer

class BasicNormalizer(
    private val lowercase: Boolean = true,
    private val trimWhitespace: Boolean = true,
    private val collapseWhitespace: Boolean = true,
    private val stripPunctuation: Boolean = false
) : Normalizer {

    private val whitespaceRegex = "\\s+".toRegex()
    private val punctuationRegex = "[\\p{Punct}]".toRegex()

    override fun normalize(text: String): String {
        var t = text
        if (lowercase) t = t.lowercase()
        if (stripPunctuation) t = t.replace(punctuationRegex, " ")
        if (collapseWhitespace) t = t.replace(whitespaceRegex, " ")
        if (trimWhitespace) t = t.trim()
        return t
    }
}
