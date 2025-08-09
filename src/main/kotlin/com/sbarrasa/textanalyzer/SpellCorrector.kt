package com.sbarrasa.textanalyzer

class SpellCorrector (
    val text: List<String>,
    val config: Config = Config()) {

    data class Config(
        val maxDistThreshold: Int = 3,
        val propDistDivisor: Int = 2,
        val lengthDiffTolerance: Int = 2
    )

    fun findBestMatch(token: String): String {
        if (token.isBlank()) return token
        
        val lowercaseToken = token.lowercase()
        
        if (text.contains(lowercaseToken)) return lowercaseToken
        
        var bestMatch = token  
        var minDistance = Int.MAX_VALUE  
        
        val maxAllowedDistance = minOf(config.maxDistThreshold, token.length / config.propDistDivisor)
        
        text.forEach { vocabWord ->
            if (kotlin.math.abs(vocabWord.length - lowercaseToken.length) <= config.lengthDiffTolerance) {
                val distance = levenshteinDistance(lowercaseToken, vocabWord)
                
                if (distance < minDistance && distance <= maxAllowedDistance) {
                    minDistance = distance
                    bestMatch = vocabWord
                }
            }
        }
        
        return bestMatch
    }
    
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
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1,
                            dp[i - 1][j - 1] + cost
                        )
                    }
                }
            }
        }
        
        return dp[len1][len2]
    }
}
