package com.sbarrasa.textanalyzer

import kotlin.math.pow

class ScoreAggregator(
   private val weightExponent: Double = 5.0,        // 5.0 = penalización muy fuerte a vecinos distantes
   private val normalizeWeightsByMax: Boolean = true  // true normaliza pesos para mayor control sobre vecinos distantes
) {

   fun aggregate(neighbors: List<Neighbor>, defaultScore: Double): Double {
      if (neighbors.isEmpty()) return defaultScore

      val maximumRawWeight = if (normalizeWeightsByMax) {
         neighbors.maxOf { it.weight }.takeIf { it > 0.0 } ?: return defaultScore
      } else {
         1.0 // evita división cuando no se normaliza
      }

      var weightedSumOfScores = 0.0
      var totalEffectiveWeight = 0.0

      for (neighbor in neighbors) {
         val baseWeight = (neighbor.weight / maximumRawWeight).coerceAtLeast(0.0)
         val effectiveWeight = if (weightExponent == 1.0) baseWeight else baseWeight.pow(weightExponent)
         if (effectiveWeight > 0.0) {
            weightedSumOfScores += neighbor.score * effectiveWeight
            totalEffectiveWeight += effectiveWeight
         }
      }

      return if (totalEffectiveWeight > 0.0) {
         weightedSumOfScores / totalEffectiveWeight
      } else {
         defaultScore
      }
   }
}