package com.sbarrasa.textanalyzer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import org.slf4j.LoggerFactory

class TextAnalyzerTest {
    
    private val log = LoggerFactory.getLogger(TextAnalyzerTest::class.java)
    
    @Test
    fun isTrainedInitiallyFalseTest() {
        val analyzer = TextAnalyzer()
        assertFalse(analyzer.isTrained, "Should not be trained initially")
    }
    
    @Test
    fun trainBasicTest() {
        val analyzer = TextAnalyzer()
        val trainingSet: TrainingSet = mapOf(
            "positive text example" to 1.0,
            "negative text example" to 0.0,
            "neutral text here" to 0.5
        )
        
        analyzer.train(trainingSet)
        assertTrue(analyzer.isTrained, "Should be trained after training")
    }
    
    @Test
    fun analyzeWithoutTrainingTest() {
        val analyzer = TextAnalyzer()
        
        assertFailsWith<IllegalStateException> {
            analyzer.analyze("some text")
        }
    }

    @Test
    fun trainWithDifferentNumberTypesTest() {
        val analyzer = TextAnalyzer()
        val trainingSet: TrainingSet = mapOf(
            "integer example" to 5,
            "double example" to 3.14,
            "float example" to 2.5f,
            "long example" to 100L
        )
        
        analyzer.train(trainingSet)
        assertTrue(analyzer.isTrained)
        
        // Should be able to analyze after training with mixed number types
        val result = analyzer.analyze("integer example")
        assertEquals(5.0, result, 0.001)
    }
    
    @Test
    fun trainWithEmptyTrainingSetTest() {
        val analyzer = TextAnalyzer()
        val emptyTrainingSet: TrainingSet = emptyMap()
        
        analyzer.train(emptyTrainingSet)
        // Should handle empty training set gracefully
        assertFalse(analyzer.isTrained, "Should not be considered trained with empty data")
    }
    
    @Test
    fun trainWithSingleExampleTest() {
        val analyzer = TextAnalyzer()
        val singleExample: TrainingSet = mapOf("single text" to 42)
        
        analyzer.train(singleExample)
        assertTrue(analyzer.isTrained)
        
        val result = analyzer.analyze("single text")
        assertEquals(42.0, result, 0.001)
        
        // Test with different text
        val result2 = analyzer.analyze("different text")
        log.debug("Single example, different text: $result2")
    }
    
    @Test
    fun trainWithDuplicateTextsTest() {
        val analyzer = TextAnalyzer()
        val trainingSet: TrainingSet = mapOf(
            "duplicate text" to 1.0,
            "another text" to 2.0,
            "duplicate text" to 3.0  // This will overwrite the first one
        )
        
        analyzer.train(trainingSet)
        assertTrue(analyzer.isTrained)
        
        val result = analyzer.analyze("duplicate text")
        assertEquals(3.0, result, 0.001, "Should use the last value for duplicate keys")
    }
    
    @Test
    fun trainWithEmptyStringsTest() {
        val analyzer = TextAnalyzer()
        val trainingSet: TrainingSet = mapOf(
            "" to 1.0,
            "normal text" to 2.0,
            "   " to 3.0  // Whitespace-only
        )
        
        analyzer.train(trainingSet)
        assertTrue(analyzer.isTrained)
        
        val result1 = analyzer.analyze("")
        val result2 = analyzer.analyze("   ")
        val result3 = analyzer.analyze("normal text")
        
        log.debug("Empty string analysis: $result1")
        log.debug("Whitespace analysis: $result2") 
        log.debug("Normal text analysis: $result3")
    }
    
    @Test
    fun retrainTest() {
        val analyzer = TextAnalyzer()
        
        // First training
        val firstTraining: TrainingSet = mapOf(
            "text one" to 1.0,
            "text two" to 2.0
        )
        analyzer.train(firstTraining)
        assertTrue(analyzer.isTrained)
        
        val firstResult = analyzer.analyze("text one")
        assertEquals(1.0, firstResult, 0.001)
        
        // Retrain with different data
        val secondTraining: TrainingSet = mapOf(
            "text one" to 10.0,  // Same text, different target
            "text three" to 30.0
        )
        analyzer.train(secondTraining)
        assertTrue(analyzer.isTrained)
        
        val secondResult = analyzer.analyze("text one")
        assertEquals(10.0, secondResult, 0.001, "Should reflect new training data")
    }
    
    @Test
    fun trainWithLargeDatasetTest() {
        val analyzer = TextAnalyzer()
        
        // Generate larger training set
        val trainingSet: TrainingSet = (1..50).associate { i ->
            "sample text number $i with some content" to (i * 0.1)
        }
        
        analyzer.train(trainingSet)
        assertTrue(analyzer.isTrained)
        
        // Test exact matches
        val result1 = analyzer.analyze("sample text number 10 with some content")
        assertEquals(1.0, result1, 0.001)
        
        val result2 = analyzer.analyze("sample text number 25 with some content") 
        assertEquals(2.5, result2, 0.001)
        
        // Test similar text
        val result3 = analyzer.analyze("sample text number 15")
        log.debug("Similar text analysis: $result3")
    }
    
    @Test
    fun analyzeWithSpecialCharactersTest() {
        val analyzer = TextAnalyzer()
        val trainingSet: TrainingSet = mapOf(
            "hello@world.com test!" to 1.0,
            "special chars: #$%^&*()" to 2.0,
            "unicode: café résumé" to 3.0,
            "numbers 123-456-7890" to 4.0
        )
        
        analyzer.train(trainingSet)
        
        val result1 = analyzer.analyze("hello@world.com test!")
        assertEquals(1.0, result1, 0.001)
        
        val result2 = analyzer.analyze("special chars: #$%^&*()")
        assertEquals(2.0, result2, 0.001)
        
        val result3 = analyzer.analyze("unicode: café résumé")
        assertEquals(3.0, result3, 0.001)
        
        log.debug("Special characters handled successfully")
    }
    
    @Test
    fun analyzeEdgeCasesTest() {
        val analyzer = TextAnalyzer()
        val trainingSet: TrainingSet = mapOf(
            "normal text" to 1.0,
            "short" to 2.0,
            "a" to 3.0
        )
        
        analyzer.train(trainingSet)
        
        // Test with very short input
        val shortResult = analyzer.analyze("a")
        assertEquals(3.0, shortResult, 0.001)
        
        // Test with long input
        val longText = "this is a very long text that was not in the training set but should still get analyzed"
        val longResult = analyzer.analyze(longText)
        log.debug("Long text analysis: $longResult")
        
        // Test with repeated words
        val repeatedResult = analyzer.analyze("normal normal normal text text")
        log.debug("Repeated words analysis: $repeatedResult")
    }
}