package textregressor

import smile.nlp.tokenizer.SimpleTokenizer

class TextFeatureExtractor {
    private val tokenizer = SimpleTokenizer(true)
    private var vocabulary: List<String> = emptyList()
    
    fun buildVocabulary(texts: Collection<String>) {
        val tokenFrequency = mutableMapOf<String, Int>()
        texts.forEach { text ->
            tokenizer.split(text.lowercase()).forEach { token ->
                tokenFrequency[token] = tokenFrequency.getOrDefault(token, 0) + 1
            }
        }
        
        vocabulary = tokenFrequency.entries
            .sortedByDescending { it.value }
            .take(10)
            .map { it.key }
            .sorted()
    }
    
    fun extractFeatures(text: String): DoubleArray {
        if (vocabulary.isEmpty()) throw IllegalStateException("Vocabulary not built")
        val tokens = tokenizer.split(text.lowercase())
        val features = DoubleArray(vocabulary.size)
        
        tokens.forEach { token ->
            val index = vocabulary.indexOf(token)
            if (index >= 0) {
                features[index] += 1.0
            }
        }
        
        return features
    }
    
    fun extractFeaturesMatrix(texts: List<String>): Array<DoubleArray> {
        return texts.map { extractFeatures(it) }.toTypedArray()
    }
}