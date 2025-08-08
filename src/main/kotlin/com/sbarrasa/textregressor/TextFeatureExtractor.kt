package com.sbarrasa.textregressor

import smile.nlp.tokenizer.SimpleTokenizer

class TextFeatureExtractor {
   private val tokenizer = SimpleTokenizer(true)
   private var vocabulary: List<String> = emptyList()

   fun buildVocabulary(texts: Collection<String>) {
      val tokenFrequency = mutableMapOf<String, Int>()
      texts.forEach { text ->
         val tokens = tokenizer.split(text)
         tokens.forEach { token ->
            if (token.isNotBlank()) {
               tokenFrequency[token] = tokenFrequency.getOrDefault(token, 0) + 1
            }
         }
         for (i in 0 until tokens.size - 1) {
            val bigram = "${tokens[i]} ${tokens[i + 1]}"
            if (tokens[i].isNotBlank() && tokens[i + 1].isNotBlank()) {
               tokenFrequency[bigram] = tokenFrequency.getOrDefault(bigram, 0) + 1
            }
         }
      }
      
      vocabulary = tokenFrequency.entries
         .sortedByDescending { it.value }
         .take(100)
         .map { it.key }
         .sorted()
   }
   
   fun extractFeatures(text: String): DoubleArray {
      if (vocabulary.isEmpty()) throw IllegalStateException("Vocabulary not built")
      val tokens = tokenizer.split(text)
      val features = DoubleArray(vocabulary.size)
      
      tokens.forEach { token ->
         val index = vocabulary.indexOf(token)
         if (index >= 0) {
            features[index] += 1.0
         }
      }
      
      for (i in 0 until tokens.size - 1) {
         val bigram = "${tokens[i]} ${tokens[i + 1]}"
         val index = vocabulary.indexOf(bigram)
         if (index >= 0) {
            features[index] += 1.0
         }
      }
      
      return features
   }
   
   fun extractFeaturesMatrix(texts: Collection<String>): Array<DoubleArray> {
      return texts.map { extractFeatures(it) }.toTypedArray()
   }
   
   fun getVocabulary(): List<String> = vocabulary
}