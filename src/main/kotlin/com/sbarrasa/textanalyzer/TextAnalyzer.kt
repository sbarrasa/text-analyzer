package com.sbarrasa.textanalyzer

class TextAnalyzer {
   private val featureExtractor = TextFeatureExtractor()
   private val regressionModel = KnnRegressor()
   
   val isTrained: Boolean
      get() = regressionModel.isTrained && featureExtractor.getVocabulary().isNotEmpty()

   constructor()
   
   constructor(trainingSet: TrainingSet) {
      train(trainingSet)
   }

   fun train(examples: TrainingSet) {
      val texts = examples.keys
      val targets = examples.values.map { it.toDouble() }.toDoubleArray()
      
      featureExtractor.buildVocabulary(texts)
      val features = featureExtractor.extractFeaturesMatrix(texts)
      
      regressionModel.train(features, targets)
   }
   
   fun analyze(text: String): Double {
      if (!isTrained) throw IllegalStateException("Model must be trained before analysis.")
      val features = featureExtractor.extractFeatures(text)
      return regressionModel.predict(features)
   }
}
