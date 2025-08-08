package com.sbarrasa.textregressor

typealias TrainingSet = Map<String, Number>

class TextRegressor {
    private val featureExtractor = TextFeatureExtractor()
    private val regressionModel = RegressionModelAdapter()
    private var isTrained = false

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
        isTrained = true
    }
    
    fun analyze(text: String): Double {
        if (!isTrained) {
            throw IllegalStateException("Model must be trained before analysis. Call train() method or use constructor with training data.")
        }
        val features = featureExtractor.extractFeatures(text)
        return regressionModel.predict(features)
    }
}