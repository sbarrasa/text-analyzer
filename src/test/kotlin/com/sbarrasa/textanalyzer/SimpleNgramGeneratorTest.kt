package com.sbarrasa.textanalyzer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.slf4j.LoggerFactory

class SimpleNgramGeneratorTest {
    
    private val log = LoggerFactory.getLogger(SimpleNgramGeneratorTest::class.java)
    private val generator = SimpleNgramGenerator()
    
    @Test
    fun generateUnigramsTest() {
        val tokens = listOf("hello", "world", "test")
        val result = generator.generate(tokens, 1..1)
        
        val expected = listOf("hello", "world", "test")
        assertEquals(expected, result)
        log.debug("Unigrams: $result")
    }
    
    @Test
    fun generateBigramsTest() {
        val tokens = listOf("hello", "world", "test", "case")
        val result = generator.generate(tokens, 2..2)
        
        val expected = listOf("hello world", "world test", "test case")
        assertEquals(expected, result)
        log.debug("Bigrams: $result")
    }
    
    @Test
    fun generateTrigramsTest() {
        val tokens = listOf("this", "is", "a", "test", "case")
        val result = generator.generate(tokens, 3..3)
        
        val expected = listOf("this is a", "is a test", "a test case")
        assertEquals(expected, result)
        log.debug("Trigrams: $result")
    }
    
    @Test
    fun generateMixedNgramsTest() {
        val tokens = listOf("hello", "world", "test")
        val result = generator.generate(tokens, 1..2)
        
        val expected = listOf("hello", "world", "test", "hello world", "world test")
        assertEquals(expected, result)
        log.debug("Mixed n-grams (1-2): $result")
    }
    
    @Test
    fun generateWideRangeNgramsTest() {
        val tokens = listOf("a", "b", "c", "d")
        val result = generator.generate(tokens, 1..4)
        
        val expected = listOf(
            "a", "b", "c", "d",  // 1-grams
            "a b", "b c", "c d",  // 2-grams
            "a b c", "b c d",     // 3-grams
            "a b c d"             // 4-grams
        )
        assertEquals(expected, result)
        log.debug("Wide range n-grams (1-4): $result")
    }
    
    @Test
    fun generateWithEmptyTokensTest() {
        val result = generator.generate(emptyList(), 1..3)
        assertTrue(result.isEmpty(), "Should return empty list for empty tokens")
    }
    
    @Test
    fun generateWithSingleTokenTest() {
        val tokens = listOf("single")
        
        // 1-gram should work
        val unigrams = generator.generate(tokens, 1..1)
        assertEquals(listOf("single"), unigrams)
        
        // 2-gram should be empty (not enough tokens)
        val bigrams = generator.generate(tokens, 2..2)
        assertTrue(bigrams.isEmpty(), "Should be empty when n > token count")
        
        // Mixed range should only return 1-gram
        val mixed = generator.generate(tokens, 1..3)
        assertEquals(listOf("single"), mixed)
    }
    
    @Test
    fun generateWithTwoTokensTest() {
        val tokens = listOf("first", "second")
        
        val result = generator.generate(tokens, 1..3)
        val expected = listOf("first", "second", "first second")
        assertEquals(expected, result)
        log.debug("Two tokens (1-3): $result")
    }
    
    @Test
    fun generateWithZeroOrNegativeRangeTest() {
        val tokens = listOf("hello", "world")
        
        // Zero in range should be ignored
        val resultWithZero = generator.generate(tokens, 0..2)
        val expected = listOf("hello", "world", "hello world")
        assertEquals(expected, resultWithZero)
        
        // Negative values should be ignored
        val resultWithNegative = generator.generate(tokens, -1..1)
        val expectedOnlyUnigrams = listOf("hello", "world")
        assertEquals(expectedOnlyUnigrams, resultWithNegative)
        
        // All negative/zero should return empty
        val allInvalid = generator.generate(tokens, -2..0)
        assertTrue(allInvalid.isEmpty())
    }
    
    @Test
    fun generateWithLargeNTest() {
        val tokens = listOf("a", "b", "c")
        
        // N larger than token count should not produce n-grams
        val result = generator.generate(tokens, 5..5)
        assertTrue(result.isEmpty(), "Should be empty when n > token count")
        
        // Mixed range where some n values are too large
        val mixedResult = generator.generate(tokens, 2..5)
        val expected = listOf("a b", "b c", "a b c") // 2-grams and 3-gram possible
        assertEquals(expected, mixedResult)
    }
    
    @Test
    fun generatePreservesTokenOrderTest() {
        val tokens = listOf("z", "a", "m", "b")
        val result = generator.generate(tokens, 2..2)
        
        val expected = listOf("z a", "a m", "m b")
        assertEquals(expected, result, "Should preserve original token order")
    }
    
    @Test
    fun generateWithRepeatedTokensTest() {
        val tokens = listOf("test", "test", "case", "test")
        val result = generator.generate(tokens, 1..2)
        
        val expected = listOf(
            "test", "test", "case", "test",  // 1-grams (including duplicates)
            "test test", "test case", "case test"  // 2-grams
        )
        assertEquals(expected, result)
        log.debug("With repeated tokens: $result")
    }
    
    @Test
    fun generateWithSpecialCharactersTest() {
        val tokens = listOf("hello,", "world!", "test@case")
        val result = generator.generate(tokens, 1..2)
        
        val expected = listOf(
            "hello,", "world!", "test@case",  // 1-grams
            "hello, world!", "world! test@case"  // 2-grams
        )
        assertEquals(expected, result)
        log.debug("With special characters: $result")
    }
    
    @Test
    fun generateWithEmptyStringsInTokensTest() {
        val tokens = listOf("hello", "", "world")
        val result = generator.generate(tokens, 1..2)
        
        val expected = listOf(
            "hello", "", "world",  // 1-grams (including empty string)
            "hello ", " world"  // 2-grams (space-separated, including empty)
        )
        assertEquals(expected, result)
    }
    
    @Test
    fun generateReverseRangeTest() {
        val tokens = listOf("a", "b", "c")
        
        // Reverse range should still work (IntRange handles this)
        val result = generator.generate(tokens, 2..1)
        assertTrue(result.isEmpty(), "Reverse range should produce no results")
    }
}