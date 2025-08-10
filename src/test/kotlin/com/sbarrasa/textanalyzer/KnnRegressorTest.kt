package com.sbarrasa.textanalyzer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import org.slf4j.LoggerFactory

class KnnRegressorTest {
    
    private val log = LoggerFactory.getLogger(KnnRegressorTest::class.java)
    
    @Test
    fun constructorValidationTest() {
        // Valid k values
        assertTrue(KnnRegressor(1).toString().isNotEmpty())
        assertTrue(KnnRegressor(5).toString().isNotEmpty())
        assertTrue(KnnRegressor(10).toString().isNotEmpty())
        
        // Invalid k values
        assertFailsWith<IllegalArgumentException> {
            KnnRegressor(0)
        }
        assertFailsWith<IllegalArgumentException> {
            KnnRegressor(-1)
        }
    }
    
    @Test
    fun isTrainedTest() {
        val regressor = KnnRegressor()
        assertFalse(regressor.isTrained, "Should not be trained initially")
        
        val features = arrayOf(
            doubleArrayOf(1.0, 2.0),
            doubleArrayOf(3.0, 4.0)
        )
        val targets = doubleArrayOf(1.5, 3.5)
        
        regressor.train(features, targets)
        assertTrue(regressor.isTrained, "Should be trained after calling train()")
    }
    
    @Test
    fun trainValidationTest() {
        val regressor = KnnRegressor()
        
        // Mismatched sizes
        assertFailsWith<IllegalArgumentException> {
            regressor.train(
                arrayOf(doubleArrayOf(1.0, 2.0)),
                doubleArrayOf(1.0, 2.0) // Too many targets
            )
        }
        
        // Inconsistent feature dimensions
        assertFailsWith<IllegalArgumentException> {
            regressor.train(
                arrayOf(
                    doubleArrayOf(1.0, 2.0),
                    doubleArrayOf(3.0) // Wrong dimension
                ),
                doubleArrayOf(1.0, 2.0)
            )
        }
        
        // Valid training data
        regressor.train(
            arrayOf(
                doubleArrayOf(1.0, 2.0),
                doubleArrayOf(3.0, 4.0)
            ),
            doubleArrayOf(1.5, 3.5)
        )
        assertTrue(regressor.isTrained)
    }
    
    @Test
    fun predictWithoutTrainingTest() {
        val regressor = KnnRegressor()
        
        assertFailsWith<IllegalStateException> {
            regressor.predict(doubleArrayOf(1.0, 2.0))
        }
    }
    
    @Test
    fun predictWithWrongDimensionTest() {
        val regressor = KnnRegressor()
        regressor.train(
            arrayOf(doubleArrayOf(1.0, 2.0)),
            doubleArrayOf(1.5)
        )
        
        assertFailsWith<IllegalArgumentException> {
            regressor.predict(doubleArrayOf(1.0)) // Wrong dimension
        }
        
        assertFailsWith<IllegalArgumentException> {
            regressor.predict(doubleArrayOf(1.0, 2.0, 3.0)) // Wrong dimension
        }
    }
    
    @Test
    fun predictExactMatchTest() {
        val regressor = KnnRegressor(k = 3)
        val features = arrayOf(
            doubleArrayOf(1.0, 0.0),
            doubleArrayOf(0.0, 1.0),
            doubleArrayOf(1.0, 1.0),
            doubleArrayOf(1.0, 0.0) // Duplicate for exact match test
        )
        val targets = doubleArrayOf(10.0, 20.0, 30.0, 15.0)
        
        regressor.train(features, targets)
        
        // Exact match should return average of matching entries
        val result = regressor.predict(doubleArrayOf(1.0, 0.0))
        val expected = (10.0 + 15.0) / 2.0 // Average of indices 0 and 3
        assertEquals(expected, result, 0.001)
        log.debug("Exact match prediction: $result")
    }
    
    @Test
    fun predictCosineSimilarityTest() {
        val regressor = KnnRegressor(k = 2)
        val features = arrayOf(
            doubleArrayOf(1.0, 0.0), // Target: 10.0
            doubleArrayOf(0.0, 1.0), // Target: 20.0
            doubleArrayOf(-1.0, 0.0), // Target: 5.0
            doubleArrayOf(0.0, -1.0)  // Target: 15.0
        )
        val targets = doubleArrayOf(10.0, 20.0, 5.0, 15.0)
        
        regressor.train(features, targets)
        
        // Test vector close to (1,0)
        val result = regressor.predict(doubleArrayOf(0.8, 0.2))
        log.debug("Cosine similarity prediction: $result")
        
        // Should be closer to first vector than others
        assertTrue(result > 8.0, "Result should be closer to target of (1,0)")
    }
    
    @Test
    fun predictWithKLargerThanDataTest() {
        val regressor = KnnRegressor(k = 10) // k larger than training data
        val features = arrayOf(
            doubleArrayOf(1.0, 0.0),
            doubleArrayOf(0.0, 1.0)
        )
        val targets = doubleArrayOf(10.0, 20.0)
        
        regressor.train(features, targets)
        
        // Should use all available neighbors when k > data size
        val result = regressor.predict(doubleArrayOf(0.5, 0.5))
        log.debug("K > data size prediction: $result")
        assertTrue(result in 10.0..20.0, "Result should be between target values")
    }
    
    @Test
    fun predictWithZeroVectorsTest() {
        val regressor = KnnRegressor(k = 1)
        val features = arrayOf(
            doubleArrayOf(0.0, 0.0),
            doubleArrayOf(1.0, 1.0)
        )
        val targets = doubleArrayOf(5.0, 15.0)
        
        regressor.train(features, targets)
        
        // Predicting zero vector should return default when no valid similarities
        val result = regressor.predict(doubleArrayOf(0.0, 0.0))
        assertEquals(5.0, result, 0.001) // Should match exact zero vector
    }
    
    @Test
    fun predictWithSingleTrainingExampleTest() {
        val regressor = KnnRegressor(k = 3)
        val features = arrayOf(doubleArrayOf(1.0, 2.0))
        val targets = doubleArrayOf(42.0)
        
        regressor.train(features, targets)
        
        // Any prediction should be influenced heavily by the single example
        val result1 = regressor.predict(doubleArrayOf(1.0, 2.0)) // Exact match
        assertEquals(42.0, result1, 0.001)
        
        val result2 = regressor.predict(doubleArrayOf(2.0, 4.0)) // Proportional
        log.debug("Single example prediction: $result2")
        assertTrue(result2 > 0.0)
    }
    
    @Test
    fun predictDefaultValueTest() {
        val regressor = KnnRegressor(k = 1)
        // Create scenario where cosine similarity would be 0 or negative
        val features = arrayOf(
            doubleArrayOf(1.0, 0.0),
            doubleArrayOf(-1.0, 0.0)
        )
        val targets = doubleArrayOf(10.0, 20.0)
        
        regressor.train(features, targets)
        
        // Vector perpendicular to training vectors might get default value
        val result = regressor.predict(doubleArrayOf(0.0, 1.0))
        log.debug("Perpendicular vector prediction: $result")
        // Should still work with cosine similarity
        assertTrue(result >= 0.0)
    }
    
    @Test
    fun trainWithEmptyDataTest() {
        val regressor = KnnRegressor()
        val emptyFeatures = emptyArray<DoubleArray>()
        val emptyTargets = doubleArrayOf()
        
        // Empty data should be valid
        regressor.train(emptyFeatures, emptyTargets)
        assertTrue(regressor.isTrained)
        
        // But prediction should fail due to dimension validation
        assertFailsWith<IllegalArgumentException> {
            regressor.predict(doubleArrayOf(1.0))
        }
    }
}