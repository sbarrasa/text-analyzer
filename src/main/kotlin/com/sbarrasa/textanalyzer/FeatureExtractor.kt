package com.sbarrasa.textanalyzer

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.es.SpanishAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import java.io.StringReader

class FeatureExtractor {
   private val analyzer: Analyzer = SpanishAnalyzer()
   var vocabulary: List<String> = emptyList()
      private set
   private var termIndex: Map<String, Int> = emptyMap()

   fun build(texts: Collection<String>) {
      val allTerms = mutableSetOf<String>()
      
      texts.forEach { text ->
         val tokens = analyzeText(text)
         allTerms.addAll(tokens)
         
         // Add bigrams
         val bigrams = generateBigrams(tokens)
         allTerms.addAll(bigrams)
      }
      
      vocabulary = allTerms.sorted()
      termIndex = vocabulary.withIndex().associate { (i, term) -> term to i }
   }

   fun extractFeatures(text: String): DoubleArray {
      ensureVocabularyBuilt()
      
      val featureVector = DoubleArray(vocabulary.size)
      val tokens = analyzeText(text)
      
      // Count unigrams
      tokens.forEach { token ->
         incrementIfExists(token, featureVector)
      }
      
      // Count bigrams
      val bigrams = generateBigrams(tokens)
      bigrams.forEach { bigram ->
         incrementIfExists(bigram, featureVector)
      }
      
      return featureVector
   }

   fun extractFeaturesMatrix(texts: Collection<String>): Array<DoubleArray> =
      texts.map { extractFeatures(it) }.toTypedArray()

   private fun analyzeText(text: String): List<String> {
      val tokens = mutableListOf<String>()
      val tokenStream = analyzer.tokenStream("content", StringReader(text))
      val termAttr = tokenStream.addAttribute(CharTermAttribute::class.java)
      
      try {
         tokenStream.reset()
         while (tokenStream.incrementToken()) {
            tokens.add(termAttr.toString())
         }
         tokenStream.end()
      } finally {
         tokenStream.close()
      }
      
      return tokens
   }
   
   private fun generateBigrams(tokens: List<String>): List<String> {
      if (tokens.size < 2) return emptyList()
      
      return tokens.zipWithNext { a, b -> "$a $b" }
   }

   private fun ensureVocabularyBuilt() {
      if (vocabulary.isEmpty()) {
         throw IllegalStateException("Vocabulary not built")
      }
   }

   private fun incrementIfExists(term: String, vector: DoubleArray) {
      val idx = termIndex[term]
      if (idx != null && idx >= 0) {
         vector[idx] += 1.0
      }
   }
}
