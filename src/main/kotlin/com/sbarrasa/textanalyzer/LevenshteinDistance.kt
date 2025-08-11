package com.sbarrasa.textanalyzer

class LevenshteinDistance(val source: String, val target: String) {
   fun calc(): Int {
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

fun String.levenshteinDistance(other: String) = LevenshteinDistance(this, other).calc()