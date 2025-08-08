package com.sbarrasa.textregressor

import kotlin.test.Test
import kotlin.test.assertTrue
import mu.KotlinLogging

class TextFeatureExtractorTest {
    companion object {
        private val logger = KotlinLogging.logger {}
        val extractor = TextFeatureExtractor()
        val texts = listOf(
            "hola mundo",
            "hola universo", 
            "paz mundial",
            "paz mundial",
            "el mundo es muy hermoso y lleno de oportunidades para aprender"
        )
        
        init {
          extractor.buildVocabulary(texts)
        }
    }

    @Test
    fun testSingleWordOccurrences() {
        val vocabulary = extractor.getVocabulary()
        logger.info("Extracted vocabulary (${vocabulary.size} terms): ${vocabulary.joinToString(", ")}")

        assertTrue(vocabulary.isNotEmpty())

        assertTrue(vocabulary.contains("hola"))
        assertTrue(vocabulary.contains("mundo"))
        assertTrue(vocabulary.contains("universo"))
        assertTrue(vocabulary.contains("paz"))
        assertTrue(vocabulary.contains("hermoso"))
    }

    @Test
    fun testBigrams() {
        val vocabulary = extractor.getVocabulary()
        logger.info("Bigrams en vocabulario: ${vocabulary.filter { it.contains(" ") }}")

        assertTrue(vocabulary.contains("hola mundo"))
        assertTrue(vocabulary.contains("hola universo"))
        assertTrue(vocabulary.contains("paz mundial"))
    }

    @Test
    fun testLongerPhraseAnalysis() {
        val longerText = "el mundo es muy hermoso y lleno de oportunidades para aprender"
        val vocabulary = extractor.getVocabulary()
        
        // Extract features from the longer phrase
        val features = extractor.extractFeatures(longerText)
        logger.info("Características extraídas de la frase larga: ${features.size} dimensiones")
        
        val hasFeatures = features.any { it > 0.0 }
        assertTrue(hasFeatures)
        
        assertTrue(vocabulary.contains("mundo"))
        assertTrue(vocabulary.contains("hermoso"))
        assertTrue(vocabulary.contains("oportunidades"))
    }

}