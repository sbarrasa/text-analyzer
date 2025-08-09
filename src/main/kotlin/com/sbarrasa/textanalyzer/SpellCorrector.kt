package com.sbarrasa.textanalyzer

import kotlin.math.abs

class SpellCorrector(
   val vocabulary: List<String>,
   val config: Config = Config()
) {

   data class Config(
      val maxDistThreshold: Int = 3,
      val propDistDivisor: Int = 2,
      val lengthDiffTolerance: Int = 2
   )

   // Conjunto normalizado a minúsculas para búsquedas rápidas y consistentes
   private val vocabularySet: Set<String> = vocabulary.map(String::lowercase).toSet()

   fun findBestMatch(token: String): String {
      if (token.isBlank()) return token

      val lowercaseToken = token.lowercase()
      val tokenLength = lowercaseToken.length

      // Coincidencia exacta
      if (lowercaseToken in vocabularySet) return lowercaseToken

      val maxAllowedDistance = calculateMaxAllowedDistance(tokenLength)

      var bestMatch = token
      var minDistance = Int.MAX_VALUE

      for (candidate in vocabularySet) {
         if (!isLengthCompatible(tokenLength, candidate.length)) continue
         val distance = levenshteinDistance(lowercaseToken, candidate)
         if (distance <= maxAllowedDistance && distance < minDistance) {
            minDistance = distance
            bestMatch = candidate
         }
      }

      return bestMatch
   }

   private fun calculateMaxAllowedDistance(tokenLength: Int): Int {
      require(config.propDistDivisor > 0) { "propDistDivisor must be > 0" }
      return minOf(config.maxDistThreshold, tokenLength / config.propDistDivisor)
   }

   private fun isLengthCompatible(aLen: Int, bLen: Int): Boolean =
      abs(aLen - bLen) <= config.lengthDiffTolerance

   private fun levenshteinDistance(source: String, target: String): Int {
      val len1 = source.length
      val len2 = target.length

      val dp = Array(len1 + 1) { IntArray(len2 + 1) }

      for (i in 0..len1) {
         for (j in 0..len2) {
            when {
               i == 0 -> dp[i][j] = j
               j == 0 -> dp[i][j] = i
               else -> {
                  val cost = if (source[i - 1] == target[j - 1]) 0 else 1
                  dp[i][j] = minOf(
                     dp[i - 1][j] + 1,      // eliminación
                     dp[i][j - 1] + 1,      // inserción
                     dp[i - 1][j - 1] + cost // sustitución
                  )
               }
            }
         }
      }

      return dp[len1][len2]
   }
}
