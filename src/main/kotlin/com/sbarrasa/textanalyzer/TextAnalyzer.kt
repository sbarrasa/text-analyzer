package com.sbarrasa.textanalyzer

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import java.io.StringReader

class TextAnalyzer(private val analyzer: Analyzer = StandardAnalyzer()) {


   private var trainingSet: TrainingSet = emptyMap()

   val isTrained: Boolean get() = trainingSet.isNotEmpty()

   fun train(trainingSet: TrainingSet) {
      require(trainingSet.isNotEmpty()) { "Training set cannot be empty" }
      this.trainingSet = trainingSet
   }

   fun analyze(text: String): Double {
      check(isTrained) { "Model must be trained before analysis." }
      require(text.isNotBlank()) { "Input text must not be blank." }

      trainingSet[text]?.let { return it.toDouble() }

      val similarities = trainingSet.map { (trainingText, score) ->
         val similarity = calculateTokenSimilarity(text, trainingText)
         SimilarityResult(trainingText, score.toDouble(), similarity)
      }

      // Filter and sort by similarity, taking only the most relevant matches
      val relevantSimilarities = similarities
         .filter { it.similarity > 0.05 } // Lower threshold to catch more subtle matches
         .sortedByDescending { it.similarity }
         .take(8) // Take more matches for better averaging

      if (relevantSimilarities.isEmpty()) return calculateDefaultScore()

      // Use exponential weighting to emphasize closer matches
      var weightedSum = 0.0
      var totalWeight = 0.0

      relevantSimilarities.forEach { result ->
         // Exponential weighting gives much higher importance to closer matches
         val weight = Math.pow(result.similarity, 5.0)
         weightedSum += result.score * weight
         totalWeight += weight
      }

      return if (totalWeight > 0.0) {
         weightedSum / totalWeight
      } else {
         calculateDefaultScore()
      }
   }

   private fun calculateTokenSimilarity(text1: String, text2: String): Double {
      val tokens1 = analyzeText(text1)
      val tokens2 = analyzeText(text2)

      if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0

      // Use a combination of Jaccard similarity and token overlap ratio
      val intersection = tokens1.intersect(tokens2).size
      val union = tokens1.union(tokens2).size
      val jaccardSimilarity = if (union > 0) intersection.toDouble() / union else 0.0

      // Also calculate overlap coefficient (more sensitive to partial matches)
      val minSize = minOf(tokens1.size, tokens2.size)
      val overlapCoefficient = if (minSize > 0) intersection.toDouble() / minSize else 0.0

      // Combine both metrics, giving more weight to overlap coefficient for partial matches
      return (jaccardSimilarity * 0.4 + overlapCoefficient * 0.6)
   }

   private fun analyzeText(text: String): Set<String> {
      val tokens = mutableSetOf<String>()
      try {
         val tokenStream: TokenStream = analyzer.tokenStream("content", StringReader(text))
         val termAttribute = tokenStream.addAttribute(CharTermAttribute::class.java)

         tokenStream.reset()
         while (tokenStream.incrementToken()) {
            tokens.add(termAttribute.toString())
         }
         tokenStream.close()
      } catch (_: Exception) {
         // Fallback to simple tokenization if Lucene fails
         tokens.addAll(text.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() })
      }

      return tokens
   }

   private fun calculateDefaultScore(): Double {
      return if (trainingSet.isNotEmpty()) {
         val stats = DescriptiveStatistics()
         trainingSet.values.forEach { stats.addValue(it.toDouble()) }
         stats.mean
      } else {
         0.0
      }
   }

   private data class SimilarityResult(
      val text: String,
      val score: Double,
      val similarity: Double
   )
}