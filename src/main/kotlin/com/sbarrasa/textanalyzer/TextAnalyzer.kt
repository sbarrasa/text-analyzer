package com.sbarrasa.textanalyzer

class TextAnalyzer {

   private val engine = ApacheDoccatAdapter()

   val isTrained: Boolean
      get() = engine.isTrained

   fun train(trainingSet: TrainingSet) {
      require(trainingSet.isNotEmpty()) { "Training set cannot be empty" }
      engine.train(trainingSet)
      check(isTrained) { "Model failed to train." }
   }

   fun analyze(text: String): Double {
      check(isTrained) { "Model must be trained before analysis." }
      require(text.isNotBlank()) { "Input text must not be blank." }
      return engine.predict(text)
   }
}