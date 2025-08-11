package com.sbarrasa.textanalyzer

interface SimilarityEngine {
   fun train(examples: List<Example>)
   fun find(queryText: String, k: Int): List<Neighbor>
}