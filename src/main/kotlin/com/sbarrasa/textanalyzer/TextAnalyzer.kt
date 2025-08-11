package com.sbarrasa.textanalyzer

typealias TrainingSet = Map<String, Number>

class TextAnalyzer(
   private val engine: SimilarityEngine = LuceneSimilarityEngine(),
   private val exactMatcher: ExactMatcher = LuceneExactMatcher(),
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

      exactMatcher.train(examples)
      engine.train(examples)

      isTrained = true
   }

   fun analyze(text: String): Double {
      check(isTrained) { "Model must be trained before analysis." }
      require(text.isNotBlank()) { "Input text must not be blank." }

      val q = preprocessor.normalize(text)

      exactMatcher.find(q)?.let { return it }

      val neighbors = engine.find(q, kNeighbors)
      return aggregator.aggregate(neighbors, avgScore)
   }

   override fun close() {
      (engine as? AutoCloseable)?.close()
      (exactMatcher as? AutoCloseable)?.close()
   }
}