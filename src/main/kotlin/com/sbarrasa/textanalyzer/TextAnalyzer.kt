package com.sbarrasa.textanalyzer

import com.sbarrasa.textanalyzer.lucene.LuceneTextSearchEngine

class TextAnalyzer(
   private val searchEngine: TextSearchEngine = LuceneTextSearchEngine(),
   private val aggregator: ScoreAggregator = ScoreAggregator(),
   private val kNeighbors: Int = 10
) : AutoCloseable {

   var isTrained = false
      private set

   private var avgScore = 0.0

   fun train(trainingSet: TrainingSet) {
      require(trainingSet.isNotEmpty()) { "Training set cannot be empty" }

      val examples = trainingSet.toExampleList()
      avgScore = examples.compute()

      searchEngine.train(examples)

      isTrained = true
   }

   fun analyze(text: String): Double {
      check(isTrained) { "Model must be trained before analysis." }
      require(text.isNotBlank()) { "Input text must not be blank." }

      searchEngine.findExact(text)?.let { return it }

      val neighbors = searchEngine.find(text, kNeighbors)
      return aggregator.aggregate(neighbors, avgScore)
   }

   override fun close() {
      (searchEngine as? AutoCloseable)?.close()
   }
}