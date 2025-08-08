import com.sbarrasa.textregressor.TextRegressor
import com.sbarrasa.textregressor.TrainingSet

fun main() {
    val trainingSet: TrainingSet = mapOf(
        "positive text" to 1.0,
        "negative text" to 0.0,
        "neutral text" to 0.5
    )
    
    println("Testing TextRegressor new API...")
    
    // Test 1: Constructor with training data (immediate training)
    println("\n1. Testing constructor with training data:")
    try {
        val model1 = TextRegressor(trainingSet)
        val result1 = model1.analyze("positive text")
        println("✓ Constructor with training data works. Result: $result1")
    } catch (e: Exception) {
        println("✗ Constructor with training data failed: ${e.message}")
    }
    
    // Test 2: Constructor without data + train later (deferred training)
    println("\n2. Testing constructor without data + train later:")
    try {
        val model2 = TextRegressor()
        model2.train(trainingSet)
        val result2 = model2.analyze("positive text")
        println("✓ Deferred training works. Result: $result2")
    } catch (e: Exception) {
        println("✗ Deferred training failed: ${e.message}")
    }
    
    // Test 3: Error handling - analyze before training
    println("\n3. Testing error handling (analyze before training):")
    try {
        val model3 = TextRegressor()
        model3.analyze("test text")
        println("✗ Should have thrown an exception!")
    } catch (e: IllegalStateException) {
        println("✓ Correctly throws exception when not trained: ${e.message}")
    } catch (e: Exception) {
        println("✗ Unexpected exception: ${e.message}")
    }
    
    println("\nAll tests completed!")
}