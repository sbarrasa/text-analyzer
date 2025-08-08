package com.sbarrasa.textregressor

import kotlin.math.sqrt

class RegressionModelAdapter {
   private lateinit var trainingFeatures: Array<DoubleArray>
   private lateinit var trainingTargets: DoubleArray
   var isTrained = false
   
   fun train(features: Array<DoubleArray>, targets: DoubleArray) {
      trainingFeatures = features
      trainingTargets = targets
      isTrained = true
   }
   
   fun predict(features: DoubleArray): Double {
      if (!isTrained) throw IllegalStateException("Model not trained")
      if (trainingFeatures.isEmpty()) return 0.0
      
      val exactMatches = trainingFeatures.mapIndexedNotNull { index, trainFeatures ->
         if (features.contentEquals(trainFeatures)) {
            index to 1.0
         } else null
      }
      
      if (exactMatches.isNotEmpty()) {
         return exactMatches.map { (index, _) -> trainingTargets[index] }.average()
      }
      
      val similarities = trainingFeatures.mapIndexed { index, trainFeatures ->
         val similarity = cosineSimilarity(features, trainFeatures)
         index to similarity
      }.filter { it.second > 0.0 }.sortedByDescending { it.second }
      
      if (similarities.isEmpty()) return 0.0
      
      val k = minOf(5, similarities.size)
      val topK = similarities.take(k)
      
      val weightedSum = topK.sumOf { (index, similarity) ->
         trainingTargets[index] * similarity
      }
      val weightSum = topK.sumOf { (_, similarity) -> similarity }
      
      return if (weightSum > 0) weightedSum / weightSum else 0.0
   }
   
   private fun cosineSimilarity(a: DoubleArray, b: DoubleArray): Double {
      if (a.size != b.size) return 0.0
      
      val dotProduct = a.zip(b).sumOf { (x, y) -> x * y }
      val normA = sqrt(a.sumOf { it * it })
      val normB = sqrt(b.sumOf { it * it })
      
      return if (normA > 0 && normB > 0) dotProduct / (normA * normB) else 0.0
   }

}