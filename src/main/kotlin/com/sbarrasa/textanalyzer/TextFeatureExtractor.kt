package com.sbarrasa.textanalyzer


/**
 * Extractor de características de texto para análisis de regresión
 * 
 * Esta clase se encarga de convertir texto sin procesar en vectores numéricos
 * que pueden ser utilizados por algoritmos de machine learning. Utiliza técnicas
 * de procesamiento de lenguaje natural como tokenización, construcción de vocabulario
 * y corrección ortográfica inteligente para crear representaciones vectoriales
 * robustas del texto de entrada.
 */
class TextFeatureExtractor {
   // Tokenizador que divide el texto en palabras individuales
   // El parámetro 'true' indica que debe conservar los espacios en blanco
   private val tokenizer = SimpleTokenizer(true)
   
   // Extractor de vocabulario para construir diccionarios de palabras y bigramas
   private val vocabExt = VocabularyExtractor()
   
   // Lista ordenada de palabras y bigramas que conforman nuestro vocabulario
   // Este vocabulario actúa como las "dimensiones" de nuestro vector de características
   private var vocabulary: List<String> = emptyList()
   
   // Corrector ortográfico que se inicializa después de construir el vocabulario
   private var spellCheck: SpellCorrector? = null
   
   /**
    * Construye el vocabulario a partir de una colección de textos de entrenamiento
    * 
    * Este método delega la construcción del vocabulario al VocabularyExtractor
    * e inicializa el corrector ortográfico con el vocabulario resultante.
    * 
    * @param texts Colección de textos que se usarán para construir el vocabulario
    */
   fun buildVocabulary(texts: Collection<String>) {
      // Delegar la construcción del vocabulario al extractor especializado
      vocabulary = vocabExt.buildVocabulary(texts)
      
      // Inicializar el corrector ortográfico con el nuevo vocabulario
      spellCheck = SpellCorrector(vocabulary)

   }
   
   /**
    * Extrae características numéricas de un texto individual
    * 
    * Este método convierte un texto en un vector numérico donde cada posición
    * corresponde a una palabra o bigrama del vocabulario. El valor en cada posición
    * indica cuántas veces aparece ese elemento en el texto.
    * 
    * Incluye corrección ortográfica inteligente que intenta encontrar la mejor
    * coincidencia en el vocabulario para palabras con errores tipográficos.
    * 
    * @param text El texto del cual extraer características
    * @return Vector numérico con las características extraídas
    * @throws IllegalStateException si el vocabulario no ha sido construido
    */
   fun extractFeatures(text: String): DoubleArray {
      // Verificar que el vocabulario haya sido construido previamente
      if (vocabulary.isEmpty()) throw IllegalStateException("Vocabulary not built")
      
      // Tokenizar el texto de entrada
      val tokens = tokenizer.split(text)
      
      // Crear vector de características inicializado en ceros
      // Cada posición corresponde a un elemento del vocabulario
      val features = DoubleArray(vocabulary.size)
      
      // Procesar cada token individual y contar su frecuencia
      tokens.forEach { token ->
         // Intenta corregir errores ortográficos usando el corrector especializado
         val correctedToken = spellCheck?.findBestMatch(token) ?: token
         
         // Buscar la posición de este token en el vocabulario
         val index = vocabulary.indexOf(correctedToken)
         if (index >= 0) {
            // Incrementar el contador para este token en el vector de características
            features[index] += 1.0
         }
      }
      
      // Procesar bigramas y contar su frecuencia
      for (i in 0 until tokens.size - 1) {
         val bigram = "${tokens[i]} ${tokens[i + 1]}"
         
         // Buscar la posición de este bigrama en el vocabulario
         val index = vocabulary.indexOf(bigram)
         if (index >= 0) {
            // Incrementar el contador para este bigrama en el vector de características
            features[index] += 1.0
         }
      }
      
      // Retornar el vector de características completo
      return features
   }
   
   /**
    * Extrae características de múltiples textos y las organiza en una matriz
    * 
    * Este método es útil para procesar lotes de textos de manera eficiente.
    * Cada fila de la matriz resultante corresponde a un texto, y cada columna
    * a una característica del vocabulario.
    * 
    * @param texts Colección de textos para procesar
    * @return Matriz donde cada fila es el vector de características de un texto
    */
   fun extractFeaturesMatrix(texts: Collection<String>): Array<DoubleArray> {
      // Aplicar extractFeatures a cada texto y convertir a matriz
      return texts.map { extractFeatures(it) }.toTypedArray()
   }
   
   /**
    * Obtiene una copia del vocabulario actual
    * 
    * @return Lista inmutable con todas las palabras y bigramas del vocabulario
    */
   fun getVocabulary(): List<String> = vocabulary
}
