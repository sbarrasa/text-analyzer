package com.sbarrasa.textregressor

/**
 * Simple text tokenizer implementation to replace the external smile SimpleTokenizer
 * 
 * This class provides basic text tokenization functionality that splits text into tokens
 * based on whitespace and punctuation, similar to smile's SimpleTokenizer.
 */
class SimpleTokenizer(private val keepSpaces: Boolean = false) {
    
    /**
     * Splits the input text into tokens
     * 
     * This implementation mimics smile's SimpleTokenizer behavior:
     * - Splits on whitespace and punctuation  
     * - The keepSpaces parameter is ignored as it interferes with bigram creation
     * - Treats consecutive letters/digits/apostrophes/hyphens as single words
     * 
     * @param text The text to tokenize
     * @return Array of tokens
     */
    fun split(text: String): Array<String> {
        if (text.isEmpty()) return emptyArray()
        
        return text.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .toTypedArray()
    }
}