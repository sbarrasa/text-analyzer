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
         // Intenta corregir errores ortográficos usando similaridad inteligente
         val correctedToken = findBestMatch(token)
         val index = vocabulary.indexOf(correctedToken)
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
   
   // Función inteligente para encontrar la mejor coincidencia usando distancia de Levenshtein
   private fun findBestMatch(token: String): String {
      if (token.isBlank()) return token
      
      val lowercaseToken = token.lowercase()
      
      // Si el token existe exactamente en el vocabulario, úsalo
      if (vocabulary.contains(lowercaseToken)) {
         return lowercaseToken
      }
      
      // Buscar la mejor coincidencia usando distancia de Levenshtein
      var bestMatch = token
      var minDistance = Int.MAX_VALUE
      val maxAllowedDistance = minOf(3, token.length / 2) // Máximo 3 errores o la mitad de la longitud
      
      vocabulary.forEach { vocabWord ->
         // Solo considerar palabras de longitud similar (±2 caracteres)
         if (kotlin.math.abs(vocabWord.length - lowercaseToken.length) <= 2) {
            val distance = levenshteinDistance(lowercaseToken, vocabWord)
            if (distance < minDistance && distance <= maxAllowedDistance) {
               minDistance = distance
               bestMatch = vocabWord
            }
         }
      }
      
      return bestMatch
   }
   
   // Implementación de distancia de Levenshtein para medir similaridad entre palabras
   private fun levenshteinDistance(s1: String, s2: String): Int {
      val len1 = s1.length
      val len2 = s2.length
      val dp = Array(len1 + 1) { IntArray(len2 + 1) }
      
      for (i in 0..len1) {
         for (j in 0..len2) {
            when {
               i == 0 -> dp[i][j] = j
               j == 0 -> dp[i][j] = i
               else -> {
                  val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                  dp[i][j] = minOf(
                     dp[i - 1][j] + 1,     // eliminación
                     dp[i][j - 1] + 1,     // inserción
                     dp[i - 1][j - 1] + cost // sustitución
                  )
               }
            }
         }
      }
      
      return dp[len1][len2]
   }
}