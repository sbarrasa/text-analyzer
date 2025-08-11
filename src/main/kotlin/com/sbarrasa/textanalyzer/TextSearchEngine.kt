package com.sbarrasa.textanalyzer

interface TextSearchEngine {
   fun train(examples: List<Example>)
   fun findExact(queryText: String): Double?
   fun find(queryText: String, k: Int): List<Neighbor>
}