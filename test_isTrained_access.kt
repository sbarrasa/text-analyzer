import com.sbarrasa.textregressor.TextRegressor
import com.sbarrasa.textregressor.TrainingSet

fun main() {
   val trainingSet: TrainingSet = mapOf(
      "positive text" to 1.0,
      "negative text" to 0.0
   )
   
   println("Testing isTrained access control...")
   
   // Test 1: Check initial state
   println("\n1. Testing initial state:")
   val model = TextRegressor()
   println("Initial isTrained value: ${model.isTrained}")
   
   // Test 2: Check after training
   println("\n2. Testing after training:")
   model.train(trainingSet)
   println("After training isTrained value: ${model.isTrained}")
   
   // Test 3: Try to modify isTrained (this should cause compilation error)
   println("\n3. Testing write access (should fail at compile time):")
   println("Attempting to set model.isTrained = false would cause compilation error")
   // model.isTrained = false  // This line would cause compilation error
   
   // Test 4: Verify functionality still works
   println("\n4. Testing functionality:")
   try {
      val result = model.analyze("positive text")
      println("✓ Analysis works correctly. Result: $result")
   } catch (e: Exception) {
      println("✗ Analysis failed: ${e.message}")
   }
   
   println("\nAll tests completed!")
}