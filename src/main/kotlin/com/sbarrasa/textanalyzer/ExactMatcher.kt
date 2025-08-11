package com.sbarrasa.textanalyzer

interface ExactMatcher {
   fun train(examples: List<Example>)
   fun find(queryText: String): Double?
}