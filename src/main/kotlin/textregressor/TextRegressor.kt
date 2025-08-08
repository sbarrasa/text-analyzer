package textregressor

typealias TrainingSet = Map<String, Number>

class TextRegressor {
    private val featureExtractor = TextFeatureExtractor()
    private val regressionModel = RegressionModelAdapter()

    fun train(examples: TrainingSet) {
        val texts = examples.keys.toList()
        val targets = examples.values.map { it.toDouble() }.toDoubleArray()
        
        featureExtractor.buildVocabulary(texts)
        val features = featureExtractor.extractFeaturesMatrix(texts)
        
        regressionModel.train(features, targets)
    }
    
    fun analyze(text: String): Double {
        val features = featureExtractor.extractFeatures(text)
        return regressionModel.predict(features)
    }
}