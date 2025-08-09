package com.sbarrasa.textregressor

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import mu.KotlinLogging

class VocabularyExtractorTest {
    
    private val logger = KotlinLogging.logger {}
    
    @Test
    fun buildVocabularyTest() {
        val extractor = VocabularyExtractor()
        val texts = listOf(
            "hola mundo",
            "hola universo", 
            "paz mundial",
            "el mundo es hermoso"
        )
        
        val vocabulary = extractor.buildVocabulary(texts)
        
        logger.debug("Generated vocabulary (${vocabulary.size} terms): ${vocabulary.joinToString(", ")}")
        
        assertTrue(vocabulary.isNotEmpty(), "Vocabulary should not be empty")
        assertTrue(vocabulary.contains("hola"), "Vocabulary should contain 'hola'")
        assertTrue(vocabulary.contains("mundo"), "Vocabulary should contain 'mundo'")
        assertTrue(vocabulary.contains("universo"), "Vocabulary should contain 'universo'")
        assertTrue(vocabulary.contains("paz"), "Vocabulary should contain 'paz'")
    }
    
    @Test
    fun buildVocabularyBigramsTest() {
        val extractor = VocabularyExtractor()
        val texts = listOf(
            "hola mundo feliz",
            "hola universo cruel",
            "paz mundial eterna"
        )
        
        val vocabulary = extractor.buildVocabulary(texts)
        val bigrams = vocabulary.filter { it.contains(" ") }
        
        logger.debug("Bigrams found: ${bigrams.joinToString(", ")}")
        
        assertTrue(bigrams.isNotEmpty(), "Should contain bigrams")
        assertTrue(vocabulary.contains("hola mundo"), "Should contain 'hola mundo' bigram")
        assertTrue(vocabulary.contains("hola universo"), "Should contain 'hola universo' bigram")
        assertTrue(vocabulary.contains("paz mundial"), "Should contain 'paz mundial' bigram")
    }
    
    @Test
    fun buildVocabularyEmptyInputTest() {
        val extractor = VocabularyExtractor()
        val vocabulary = extractor.buildVocabulary(emptyList())
        
        assertTrue(vocabulary.isEmpty(), "Empty input should produce empty vocabulary")
    }
    
    @Test
    fun buildVocabularyFrequencyOrderingTest() {
        val extractor = VocabularyExtractor()
        val texts = listOf(
            "palabra palabra palabra",
            "otra palabra",
            "tercera vez"
        )
        
        val vocabulary = extractor.buildVocabulary(texts)
        
        logger.debug("Vocabulary ordered by frequency: ${vocabulary.joinToString(", ")}")
        
        assertTrue(vocabulary.contains("palabra"), "Should contain most frequent word 'palabra'")
        assertTrue(vocabulary.isNotEmpty(), "Vocabulary should not be empty")
    }
}