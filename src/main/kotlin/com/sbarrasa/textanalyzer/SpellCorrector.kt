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

   private val vocabularySet = vocabulary.map(String::lowercase).toSet()

   fun findBestMatch(token: String): String {
      if (token.isBlank()) return token

      val normalizedToken = token.lowercase()
      val tokenLength = normalizedToken.length

      if (normalizedToken in vocabularySet) return normalizedToken

      val maxAllowedDistance = calculateMaxAllowedDistance(tokenLength)

      var bestMatch = token
      var minDistance = Int.MAX_VALUE

      for (candidate in vocabularySet) {
         if (!isLengthCompatible(tokenLength, candidate.length)) continue

         val distance = normalizedToken.levenshteinDistance(candidate)
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

}
