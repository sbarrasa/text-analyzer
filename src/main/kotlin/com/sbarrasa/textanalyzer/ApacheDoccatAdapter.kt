package com.sbarrasa.textanalyzer

import opennlp.tools.tokenize.SimpleTokenizer
import org.apache.commons.text.similarity.LevenshteinDistance
import kotlin.math.pow

typealias OpenNlpExamples = List<Pair<Set<String>, Double>>

class ApacheDoccatAdapter {

   private var trainingData: OpenNlpExamples = emptyList()
   private var avg: Double = 0.0

   var isTrained: Boolean = false
      private set

   val levenshteinDistance = LevenshteinDistance.getDefaultInstance()

   fun train(trainingSet: TrainingSet) {
      require(trainingSet.isNotEmpty()) { "Training set cannot be empty" }

      trainingData = trainingSet.toOpenNlpExamples()
      avg = trainingData.map { it.second }.average().takeIf { !it.isNaN() } ?: 0.0

      isTrained = true
   }

   fun predict(text: String): Double {
      check(isTrained) { "Model not trained." }

      val inputText = text.lowercase()
      
      val tokens = SimpleTokenizer.INSTANCE.tokenize(inputText)
         .toSet()
      
      if (tokens.isEmpty()) return avg

      var totalWeight = 0.0
      
      for ((trainingTokens, score) in trainingData) {
         var similarity = calculateEnhancedSimilarity(tokens, trainingTokens)
         
         trainingData.any { (trainTokens, trainScore) ->
            trainTokens.all { trainToken -> inputText.contains(trainToken, ignoreCase = true) } &&
            trainScore <= 1.0 // Only consider low priority training data
         }

         var weight = similarity.pow(2)

         totalWeight += weight
      }
      
     return totalWeight
   }
   
   private fun calculateEnhancedSimilarity(tokens1: Set<String>, tokens2: Set<String>): Double {
      val intersection = tokens1.intersect(tokens2).size
      val union = tokens1.union(tokens2).size
      
      if (union == 0) return 0.0
      
      // Enhanced Jaccard similarity with fuzzy matching boost
      val jaccardSimilarity = intersection.toDouble() / union.toDouble()

      // Add fuzzy matching boost using Levenshtein distance
      val fuzzyMatches = countFuzzyMatches(tokens1, tokens2)
      val fuzzyBoost = fuzzyMatches.toDouble() / maxOf(tokens1.size, tokens2.size)

      // Combine exact and fuzzy matching with weighted average
      return (jaccardSimilarity * 0.7) + (fuzzyBoost * 0.3)
   }

   private fun countFuzzyMatches(tokens1: Set<String>, tokens2: Set<String>): Int {
      var matches = 0
      for (token1 in tokens1) {
         for (token2 in tokens2) {
            if (token1.length >= 3 && token2.length >= 3 &&
                levenshteinDistance.apply(token1, token2) <= 3) {
               matches++
               break
            }
         }
      }
      return matches
   }

}

private fun TrainingSet.toOpenNlpExamples(): List<Pair<Set<String>, Double>> {
   return this.entries.map { (text, score) ->
      val tokens = SimpleTokenizer.INSTANCE.tokenize(text.lowercase())
         .toSet()
      tokens to score.toDouble()
   }
}
