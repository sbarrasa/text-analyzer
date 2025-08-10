package com.sbarrasa.textanalyzer

import kotlin.test.Test
import kotlin.test.assertEquals
import org.slf4j.LoggerFactory

class BasicNormalizerTest {
    
    private val log = LoggerFactory.getLogger(BasicNormalizerTest::class.java)
    
    @Test
    fun normalizeWithDefaultConfigTest() {
        val normalizer = BasicNormalizer()
        
        val testCases = mapOf(
            "  Hello World!  " to "hello world!",
            "UPPERCASE text" to "uppercase text",
            "Multiple   spaces    here" to "multiple spaces here",
            "Text\twith\ttabs" to "text with tabs",
            "Text\nwith\nnewlines" to "text with newlines",
            "" to "",
            "   " to ""
        )
        
        testCases.forEach { (input, expected) ->
            val result = normalizer.normalize(input)
            log.debug("'$input' -> '$result'")
            assertEquals(expected, result, "Failed to normalize '$input'")
        }
    }
    
    @Test
    fun normalizeWithLowercaseDisabledTest() {
        val normalizer = BasicNormalizer(lowercase = false)
        
        val result = normalizer.normalize("  Hello WORLD!  ")
        assertEquals("Hello WORLD!", result)
    }
    
    @Test
    fun normalizeWithTrimDisabledTest() {
        val normalizer = BasicNormalizer(trimWhitespace = false, lowercase = false, collapseWhitespace = false)
        
        val result = normalizer.normalize("  hello world!  ")
        assertEquals("  hello world!  ", result)
    }
    
    @Test
    fun normalizeWithCollapseWhitespaceDisabledTest() {
        val normalizer = BasicNormalizer(collapseWhitespace = false)
        
        val result = normalizer.normalize("hello   multiple    spaces")
        assertEquals("hello   multiple    spaces", result)
    }
    
    @Test
    fun normalizeWithPunctuationStrippingEnabledTest() {
        val normalizer = BasicNormalizer(stripPunctuation = true, collapseWhitespace = false, trimWhitespace = false)
        
        val testCases = mapOf(
            "Hello, World!" to "hello  world ",
            "Test: this-is a test." to "test  this is a test ",
            "No punctuation here" to "no punctuation here",
        )
        
        testCases.forEach { (input, expected) ->
            val result = normalizer.normalize(input)
            log.debug("'$input' -> '$result'")
            assertEquals(expected, result, "Failed to strip punctuation from '$input'")
        }
    }
    
    @Test
    fun normalizeWithAllOptionsDisabledTest() {
        val normalizer = BasicNormalizer(
            lowercase = false,
            trimWhitespace = false,
            collapseWhitespace = false,
            stripPunctuation = false
        )
        
        val input = "  Hello,   WORLD!  "
        val result = normalizer.normalize(input)
        assertEquals(input, result, "Should not modify text when all options are disabled")
    }
    
    @Test
    fun normalizeWithAllOptionsEnabledTest() {
        val normalizer = BasicNormalizer(
            lowercase = true,
            trimWhitespace = true,
            collapseWhitespace = true,
            stripPunctuation = true
        )
        
        val input = "  Hello,   WORLD!  Test-Case.  "
        val result = normalizer.normalize(input)
        assertEquals("hello world test case", result)
    }
    
    @Test
    fun normalizeEmptyAndWhitespaceTest() {
        val normalizer = BasicNormalizer()
        
        assertEquals("", normalizer.normalize(""))
        assertEquals("", normalizer.normalize("   "))
        assertEquals("", normalizer.normalize("\t\n\r"))
    }
    
    @Test
    fun normalizeSpecialCharactersTest() {
        val normalizer = BasicNormalizer(stripPunctuation = true, collapseWhitespace = false)
        
        val testCases = mapOf(
            "äöü ñ ç" to "äöü ñ ç", // Non-ASCII letters should remain
            "123 456" to "123 456", // Numbers should remain
            "test@example.com" to "test example com", // Punctuation should be stripped
            "unicode: 你好 世界" to "unicode  你好 世界" // Unicode characters should remain
        )
        
        testCases.forEach { (input, expected) ->
            val result = normalizer.normalize(input)
            assertEquals(expected, result, "Failed to handle special characters in '$input'")
        }
    }
}