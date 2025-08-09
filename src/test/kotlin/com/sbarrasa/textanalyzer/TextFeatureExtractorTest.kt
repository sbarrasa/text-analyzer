package com.sbarrasa.textanalyzer

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
    fun getVocabularyTest() {
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
    fun getVocabularyBigramsTest() {
        val vocabulary = extractor.getVocabulary()
        logger.info("Bigrams en vocabulario: ${vocabulary.filter { it.contains(" ") }}")

        assertTrue(vocabulary.contains("hola mundo"))
        assertTrue(vocabulary.contains("hola universo"))
        assertTrue(vocabulary.contains("paz mundial"))
    }

    @Test
    fun extractFeaturesTest() {
        val longerText = "el mundo es muy hermoso y lleno de oportunidades para aprender"
        val vocabulary = extractor.getVocabulary()
        
        // Extraer características de la frase más larga
        val features = extractor.extractFeatures(longerText)
        logger.info("Características extraídas de la frase larga: ${features.size} dimensiones")
        
        val hasFeatures = features.any { it > 0.0 }
        assertTrue(hasFeatures)
        
        assertTrue(vocabulary.contains("mundo"))
        assertTrue(vocabulary.contains("hermoso"))
        assertTrue(vocabulary.contains("oportunidades"))
    }

}
