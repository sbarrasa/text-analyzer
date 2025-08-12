package com.sbarrasa.textanalyzer

import com.sbarrasa.textanalyzer.lucene.LuceneTextSearchEngine

class TextAnalyzer(
   private val searchEngine: TextSearchEngine = LuceneTextSearchEngine(),
   private val aggregator: ScoreAggregator = ScoreAggregator(),
   private val kNeighbors: Int = 10
) : AutoCloseable {

   var isTrained = false
      private set

   fun train(trainingSet: TrainingSet) {
      require(trainingSet.isNotEmpty()) { "Training set cannot be empty" }
      searchEngine.train(trainingSet)
      isTrained = true
   }

   fun analyze(text: String): Double {
      check(isTrained) { "Model must be trained before analysis." }
      require(text.isNotBlank()) { "Input text must not be blank." }

      searchEngine.findExact(text)?.let { return it }

      val neighbors = searchEngine.find(text, kNeighbors)
      return aggregator.aggregate(neighbors, searchEngine.defaultScore())
   }

   override fun close() {
      (searchEngine as? AutoCloseable)?.close()
   }
}