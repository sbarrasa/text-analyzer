package com.sbarrasa.textanalyzer

interface ScoreAggregator {
   fun aggregate(neighbors: List<Neighbor>, defaultScore: Double): Double
}