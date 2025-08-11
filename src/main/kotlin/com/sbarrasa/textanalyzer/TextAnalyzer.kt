package com.sbarrasa.textanalyzer

class TextAnalyzer {
   private val featureExtractor = FeatureExtractor()
   private val regressionModel = KnnRegressor()

   val isTrained: Boolean
      get() = regressionModel.isTrained && featureExtractor.getVocabulary().isNotEmpty()

   fun train(examples: TrainingSet) {
      val texts = examples.keys
      val targets = examples.values.map { it.toDouble() }.toDoubleArray()

      featureExtractor.build(texts)
      val features = featureExtractor.extractFeaturesMatrix(texts)

      regressionModel.train(features, targets)
   }

   fun analyze(text: String): Double {
      check(isTrained) { "Model must be trained before analysis." }
      val features = featureExtractor.extractFeatures(text)
      return regressionModel.predict(features)
   }
}

typealias TrainingSet = Map<String, Number>
