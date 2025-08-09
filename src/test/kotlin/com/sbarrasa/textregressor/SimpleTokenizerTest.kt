package com.sbarrasa.textregressor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import mu.KotlinLogging

class SimpleTokenizerTest {
    
    private val logger = KotlinLogging.logger {}
    
    @Test
    fun splitTest() {
        val tokenizer = SimpleTokenizer(true)
        
        // Test basic tokenization
        val testCases = mapOf(
            "hola mundo" to arrayOf("hola", "mundo"),
            "hola universo cruel" to arrayOf("hola", "universo", "cruel"),
            "   espacios   extras   " to arrayOf("espacios", "extras"),
            "una-palabra-con-guiones" to arrayOf("una-palabra-con-guiones"),
            "" to emptyArray()
        )
        
        testCases.forEach { (input, expected) ->
            val result = tokenizer.split(input)
            logger.debug("Input: '$input' -> Tokens: [${result.joinToString(", ")}]")
            assertEquals(expected.size, result.size, "Token count mismatch for '$input'")
            expected.forEachIndexed { index, expectedToken ->
                assertEquals(expectedToken, result[index], "Token mismatch at position $index for '$input'")
            }
        }
    }
    
    @Test
    fun splitEmptyInputTest() {
        val tokenizer = SimpleTokenizer(true)
        
        val result = tokenizer.split("")
        assertTrue(result.isEmpty(), "Empty string should return empty array")
        
        val whitespaceResult = tokenizer.split("   ")
        assertTrue(whitespaceResult.isEmpty(), "Whitespace-only string should return empty array")
    }
    
    @Test
    fun splitWithPunctuationTest() {
        val tokenizer = SimpleTokenizer(true)
        
        val text = "Hola, mundo! ¿Cómo estás?"
        val result = tokenizer.split(text)
        
        logger.debug("Text with punctuation: '$text' -> Tokens: [${result.joinToString(", ")}]")
        
        assertTrue(result.isNotEmpty(), "Text with punctuation should produce tokens")
        assertTrue(result.contains("Hola,") || result.contains("Hola"), "Should contain 'Hola' with or without punctuation")
        assertTrue(result.contains("mundo!") || result.contains("mundo"), "Should contain 'mundo' with or without punctuation")
    }
}