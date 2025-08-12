package com.sbarrasa.textanalyzer

interface TextSearchEngine {
   fun train(trainingSet: TrainingSet)
   fun findExact(queryText: String): Double?
   fun find(queryText: String, k: Int): List<Neighbor>
   fun defaultScore(): Double
}