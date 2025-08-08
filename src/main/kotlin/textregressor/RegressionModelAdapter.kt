package textregressor

import kotlin.math.sqrt

class RegressionModelAdapter {
    private var trainingFeatures: Array<DoubleArray> = emptyArray()
    private var trainingTargets: DoubleArray = doubleArrayOf()
    private var isTrained = false
    
    fun train(features: Array<DoubleArray>, targets: DoubleArray) {
        trainingFeatures = features
        trainingTargets = targets
        isTrained = true
    }
    
    fun predict(features: DoubleArray): Double {
        if (!isTrained) throw IllegalStateException("Model not trained")
        if (trainingFeatures.isEmpty()) return 0.0
        
        val similarities = trainingFeatures.mapIndexed { index, trainFeatures ->
            val similarity = cosineSimilarity(features, trainFeatures)
            index to similarity
        }.sortedByDescending { it.second }
        
        val k = minOf(3, similarities.size)
        val topK = similarities.take(k)
        
        if (topK.isEmpty()) return 0.0
        
        val weightedSum = topK.sumOf { (index, similarity) ->
            trainingTargets[index] * (similarity + 0.1)
        }
        val weightSum = topK.sumOf { (_, similarity) -> similarity + 0.1 }
        
        return if (weightSum > 0) weightedSum / weightSum else 0.0
    }
    
    private fun cosineSimilarity(a: DoubleArray, b: DoubleArray): Double {
        if (a.size != b.size) return 0.0
        
        val dotProduct = a.zip(b).sumOf { (x, y) -> x * y }
        val normA = sqrt(a.sumOf { it * it })
        val normB = sqrt(b.sumOf { it * it })
        
        return if (normA > 0 && normB > 0) dotProduct / (normA * normB) else 0.0
    }
    
    fun isTrained(): Boolean = isTrained
}