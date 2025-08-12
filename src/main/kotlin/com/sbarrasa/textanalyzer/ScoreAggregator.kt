package com.sbarrasa.textanalyzer

import kotlin.math.pow

class ScoreAggregator {
   fun aggregate(neighbors: List<Neighbor>, defaultScore: Double): Double {
      if (neighbors.isEmpty()) return defaultScore
      
      var weightedSum = 0.0
      var total = 0.0
      for (neighbor in neighbors) {
         val w2 = neighbor.weight.pow(2)
         if (w2 > 0) {
            weightedSum += neighbor.score * w2
            total += w2
         }
      }
      
      val result = if (total > 0.0) weightedSum / total else defaultScore
      
      return result
   }
}