import com.sbarrasa.textregressor.TextFeatureExtractor

fun main() {
    println("[DEBUG_LOG] Testing intelligent typo correction...")
    
    val extractor = TextFeatureExtractor()
    val texts = listOf(
        "hola mundo",
        "bueno dia", 
        "excelente trabajo",
        "muy bien hecho"
    )
    
    println("[DEBUG_LOG] Building vocabulary with texts: $texts")
    extractor.buildVocabulary(texts)
    
    val vocabulary = extractor.getVocabulary()
    println("[DEBUG_LOG] Vocabulary built: $vocabulary")
    
    // Test typo correction
    val testTexts = listOf(
        "hola mundo",      // exact match
        "hola munod",      // typo in "mundo"
        "bueno dia",       // exact match
        "buneo dia",       // typo in "bueno" 
        "exelente trabajo" // typo in "excelente"
    )
    
    testTexts.forEach { text ->
        println("[DEBUG_LOG] Testing text: '$text'")
        try {
            val features = extractor.extractFeatures(text)
            val nonZeroCount = features.count { it > 0.0 }
            println("[DEBUG_LOG] Features extracted successfully. Non-zero features: $nonZeroCount/${features.size}")
        } catch (e: Exception) {
            println("[DEBUG_LOG] ERROR: ${e.message}")
            e.printStackTrace()
        }
    }
    
    println("[DEBUG_LOG] Test completed successfully!")
}