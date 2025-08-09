package com.sbarrasa.textanalyzer

/**
 * Extractor de características de texto para análisis de regresión.
 *
 * Convierte texto sin procesar en vectores numéricos que pueden ser utilizados por
 * algoritmos de machine learning. Utiliza tokenización, construcción de vocabulario
 * y corrección ortográfica para crear representaciones vectoriales robustas.
 */
class TextFeatureExtractor {

   private val tokenizer = SimpleTokenizer()

   private val vocabularyExtractor = VocabularyExtractor()

   private var vocabulary: List<String> = emptyList()

   private var termIndex: Map<String, Int> = emptyMap()

   private var spellCorrector: SpellCorrector? = null

   /**
    * Construye el vocabulario a partir de una colección de textos de entrenamiento
    * e inicializa el corrector ortográfico.
    */
   fun buildVocabulary(texts: Collection<String>) {
      vocabulary = vocabularyExtractor.buildVocabulary(texts)
      termIndex = vocabulary.withIndex().associate { (i, term) -> term to i }
      spellCorrector = SpellCorrector(vocabulary)
   }

   /**
    * Extrae características numéricas de un texto individual.
    *
    * @throws IllegalStateException si el vocabulario no ha sido construido
    */
   fun extractFeatures(text: String): DoubleArray {
      ensureVocabularyBuilt()

      val tokens = tokenizer.split(text)
      val featureVector = DoubleArray(vocabulary.size)

      // Unigramas
      tokens.forEach { token ->
         val corrected = spellCorrector?.findBestMatch(token) ?: token
         incrementIfExists(corrected, featureVector)
      }

      // Bigrama(s)
      tokens.asList()
         .windowed(size = 2, step = 1, partialWindows = false)
         .forEach { (a, b) ->
            val bigram = "$a $b"
            incrementIfExists(bigram, featureVector)
         }

      return featureVector
   }

   /**
    * Extrae características de múltiples textos y las organiza en una matriz (una fila por texto).
    */
   fun extractFeaturesMatrix(texts: Collection<String>): Array<DoubleArray> =
      texts.map { extractFeatures(it) }.toTypedArray()

   /**
    * Obtiene una copia del vocabulario actual.
    */
   fun getVocabulary(): List<String> = vocabulary

   // -------------------- Privado / Helpers --------------------

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
