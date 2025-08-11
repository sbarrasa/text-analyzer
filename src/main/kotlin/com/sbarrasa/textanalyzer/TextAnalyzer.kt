package com.sbarrasa.textanalyzer

import com.sbarrasa.textanalyzer.lucene.LuceneTextSearchEngine

typealias TrainingSet = Map<String, Number>

class TextAnalyzer(
   private val searchEngine: TextSearchEngine = LuceneTextSearchEngine(),
   private val aggregator: ScoreAggregator = WeightedScoreAggregator(),
   private val preprocessor: Preprocessor = IdentityPreprocessor(),
   private val kNeighbors: Int = 10
) : AutoCloseable {

   var isTrained = false
      private set

   private var avgScore = 0.0

   fun train(examples: TrainingSet) {
      require(examples.isNotEmpty()) { "Training set cannot be empty" }

      val examples = examples.map { (t, s) -> Example(preprocessor.normalize(t), s.toDouble()) }
      avgScore = examples.map { it.score }.average().takeIf { !it.isNaN() } ?: 0.0

      searchEngine.train(examples)

      isTrained = true
   }

   fun analyze(text: String): Double {
      check(isTrained) { "Model must be trained before analysis." }
      require(text.isNotBlank()) { "Input text must not be blank." }

      val q = preprocessor.normalize(text)

      searchEngine.findExact(q)?.let { return it }

      val neighbors = searchEngine.find(q, kNeighbors)
      return aggregator.aggregate(neighbors, avgScore)
   }

   override fun close() {
      (searchEngine as? AutoCloseable)?.close()
   }
}