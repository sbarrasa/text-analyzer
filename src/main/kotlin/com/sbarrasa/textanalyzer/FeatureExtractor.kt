package com.sbarrasa.textanalyzer

class FeatureExtractor(
   private val tokenizer: Tokenizer = SimpleTokenizer(),
   private val ngramGenerator: NgramGenerator = SimpleNgramGenerator()
) {

   private val vocabularyExtractor = VocabularyExtractor()

   private var vocabulary: List<String> = emptyList()
   private var termIndex: Map<String, Int> = emptyMap()
   private var spellCorrector: SpellCorrector? = null

   fun build(texts: Collection<String>) {
      vocabulary = vocabularyExtractor.build(texts)
      termIndex = vocabulary.withIndex().associate { (i, term) -> term to i }
      spellCorrector = SpellCorrector(vocabulary)
   }

   fun extractFeatures(text: String): DoubleArray {
      ensureVocabularyBuilt()

      val tokens = tokenizer.tokenize(text)
      val featureVector = DoubleArray(vocabulary.size)

      tokens.forEach { token ->
         val corrected = spellCorrector?.findBestMatch(token) ?: token
         incrementIfExists(corrected, featureVector)
      }

      val bigrams = ngramGenerator.generate(tokens, 2..2)
      bigrams.forEach { ngram ->
         incrementIfExists(ngram, featureVector)
      }

      return featureVector
   }

   fun extractFeaturesMatrix(texts: Collection<String>): Array<DoubleArray> =
      texts.map { extractFeatures(it) }.toTypedArray()

   fun getVocabulary(): List<String> = vocabulary

   private fun ensureVocabularyBuilt() {
      if (vocabulary.isEmpty()) {
         throw IllegalStateException("Vocabulary not built")
      }
   }

   private fun incrementIfExists(term: String, vector: DoubleArray) {
      val idx = termIndex[term]
      if (idx != null && idx >= 0) {
         vector[idx] += 1.0
      }
   }
}
