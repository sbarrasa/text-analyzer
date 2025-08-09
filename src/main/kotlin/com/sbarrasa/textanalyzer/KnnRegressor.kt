package com.sbarrasa.textanalyzer

import kotlin.math.sqrt

/**
 * Regresor basado en algoritmo K-Nearest Neighbors (KNN)
 * 
 * Esta clase implementa un modelo de regresión basado en vecinos más cercanos que
 * utiliza similaridad coseno para encontrar ejemplos similares en el conjunto de
 * entrenamiento y realizar predicciones. Es especialmente útil para problemas donde
 * la relación entre características y valores objetivo no es lineal.
 * 
 * El algoritmo funciona de la siguiente manera:
 * 1. Almacena todos los ejemplos de entrenamiento (características y valores objetivo)
 * 2. Para cada predicción, encuentra los K ejemplos más similares usando similaridad coseno
 * 3. Calcula una predicción ponderada basada en la similaridad de los vecinos
 */
class KnnRegressor {
   // Matriz de características de entrenamiento donde cada fila representa un ejemplo
   // y cada columna una característica extraída del texto
   private lateinit var trainFeats: Array<DoubleArray>
   
   // Vector de valores objetivo correspondientes a cada ejemplo de entrenamiento
   private lateinit var trainTargs: DoubleArray
   
   /**
    * Propiedad que indica si el modelo ha sido entrenado
    * 
    * Verifica que tanto las características como los valores objetivo hayan sido
    * inicializados correctamente. Es esencial verificar esto antes de realizar
    * predicciones para evitar errores.
    * 
    * @return true si el modelo está entrenado, false en caso contrario
    */
   val isTrained: Boolean
      get() = ::trainFeats.isInitialized && ::trainTargs.isInitialized
   
   /**
    * Entrena el modelo almacenando los ejemplos de entrenamiento
    * 
    * Este método no realiza cálculos complejos sino que simplemente almacena
    * todos los ejemplos de entrenamiento para su uso posterior durante la predicción.
    * Es un enfoque de "aprendizaje perezoso" donde el procesamiento real ocurre
    * en el momento de la predicción.
    * 
    * @param features Matriz de características donde cada fila es un ejemplo
    * @param targets Vector de valores objetivo correspondientes a cada ejemplo
    */
   fun train(features: Array<DoubleArray>, targets: DoubleArray) {
      // Almacenar las características de entrenamiento
      trainFeats = features
      
      // Almacenar los valores objetivo de entrenamiento
      trainTargs = targets
   }
   
   /**
    * Realiza una predicción usando el algoritmo K-Nearest Neighbors con similaridad coseno
    * 
    * Este método implementa la lógica central del algoritmo KNN:
    * 1. Primero busca coincidencias exactas en los datos de entrenamiento
    * 2. Si no las encuentra, calcula la similaridad coseno con todos los ejemplos
    * 3. Selecciona los K ejemplos más similares (K=5 por defecto)
    * 4. Calcula una predicción ponderada basada en la similaridad
    * 
    * La predicción ponderada da más peso a los ejemplos más similares, lo que
    * resulta en predicciones más precisas y robustas.
    * 
    * @param features Vector de características del ejemplo a predecir
    * @return Valor predicho basado en los vecinos más cercanos
    * @throws IllegalStateException si el modelo no ha sido entrenado
    */
   fun predict(features: DoubleArray): Double {
      // Verificar que el modelo haya sido entrenado previamente
      if (!isTrained) throw IllegalStateException("Model not trained")

      // PASO 1: Buscar coincidencias exactas en el conjunto de entrenamiento
      // Si encontramos ejemplos idénticos, podemos devolver su promedio directamente
      val exactMatches = trainFeats.mapIndexedNotNull { index, trainFeatures ->
         if (features.contentEquals(trainFeatures)) {
            index to 1.0  // Similaridad perfecta = 1.0
         } else null
      }
      
      // Si hay coincidencias exactas, retornar el promedio de sus valores objetivo
      if (exactMatches.isNotEmpty()) {
         return exactMatches.map { (index, _) -> trainTargs[index] }.average()
      }
      
      // PASO 2: Calcular similaridad coseno con todos los ejemplos de entrenamiento
      val similarities = trainFeats.mapIndexed { index, trainFeatures ->
         val similarity = cosineSimilarity(features, trainFeatures)
         index to similarity  // Guardar el índice y su similaridad
      }.filter { it.second > 0.0 }  // Filtrar similaridades positivas
       .sortedByDescending { it.second }  // Ordenar por similaridad descendente
      
      // Si no hay ejemplos similares, devolver predicción por defecto
      if (similarities.isEmpty()) return 0.0
      
      // PASO 3: Seleccionar los K vecinos más cercanos (K = 5)
      val k = minOf(5, similarities.size)  // Usar máximo 5 vecinos o todos si hay menos
      val topK = similarities.take(k)
      
      // PASO 4: Calcular predicción ponderada
      // Suma ponderada: cada vecino contribuye proporcionalmente a su similaridad
      val weightedSum = topK.sumOf { (index, similarity) ->
         trainTargs[index] * similarity
      }
      
      // Suma total de pesos para normalizar
      val weightSum = topK.sumOf { (_, similarity) -> similarity }
      
      // Retornar predicción normalizada o 0 si no hay pesos válidos
      return if (weightSum > 0) weightedSum / weightSum else 0.0
   }
   
   private fun cosineSimilarity(a: DoubleArray, b: DoubleArray): Double {
      if (a.size != b.size) return 0.0
      
      val dotProduct = a.zip(b).sumOf { (x, y) -> x * y }
      val normA = sqrt(a.sumOf { it * it })
      val normB = sqrt(b.sumOf { it * it })
      
      return if (normA > 0 && normB > 0) dotProduct / (normA * normB) else 0.0
   }

}
