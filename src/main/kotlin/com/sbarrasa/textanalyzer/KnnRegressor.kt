package com.sbarrasa.textanalyzer

import kotlin.math.sqrt

/**
 * Regresor basado en algoritmo K-Nearest Neighbors (KNN)
 *
 * Implementa un KNN regresor con similaridad coseno.
 */
class KnnRegressor {
   private var k: Int

   constructor(k: Int = 5) {
      require(k > 0) { "k debe ser mayor que 0" }
      this.k = k
   }

   // Matriz de características de entrenamiento: una fila por ejemplo
   private lateinit var trainingFeatures: Array<DoubleArray>

   // Vector de valores objetivo correspondientes a cada ejemplo de entrenamiento
   private lateinit var trainingTargets: DoubleArray

   private companion object {
      const val DEFAULT_PREDICTION = 0.0
      const val MIN_SIMILARITY_EPS = 1e-12
   }

   val isTrained: Boolean
      get() = ::trainingFeatures.isInitialized && ::trainingTargets.isInitialized

   /**
    * Entrena el modelo almacenando los ejemplos de entrenamiento.
    * Valida tamaños y coherencia dimensional.
    */
   fun train(features: Array<DoubleArray>, targets: DoubleArray) {
      validateFeatureMatrix(features, targets)
      trainingFeatures = features
      trainingTargets = targets
   }

   /**
    * Realiza una predicción usando KNN con similaridad coseno.
    */
   fun predict(features: DoubleArray): Double {
      requireTrainedAndDimensions(features)

      // 1) Coincidencias exactas
      val exactMatchIndices = findExactMatches(features)
      if (exactMatchIndices.isNotEmpty()) return averageTargetOf(exactMatchIndices)

      // 2) Similaridades coseno
      val similarities = cosineSimilarities(features)
      if (similarities.isEmpty()) return DEFAULT_PREDICTION

      // 3) Vecinos más cercanos
      val topK = topKNeighbors(similarities)

      // 4) Predicción ponderada
      return weightedPrediction(topK)
   }

   private fun validateFeatureMatrix(features: Array<DoubleArray>, targets: DoubleArray) {
      require(features.size == targets.size) {
         "features y targets deben tener la misma cantidad de ejemplos"
      }
      if (features.isNotEmpty()) {
         val expectedSize = features[0].size
         require(features.all { it.size == expectedSize }) {
            "Todas las filas de features deben tener la misma longitud ($expectedSize)"
         }
      }
   }

   private fun requireTrainedAndDimensions(sample: DoubleArray) {
      check(isTrained) { "El modelo debe estar entrenado antes de predecir." }
      val expected = trainingFeatures.firstOrNull()?.size ?: 0
      require(sample.size == expected) {
         "Dimensión de features inválida: se esperaba $expected y se recibió ${sample.size}"
      }
   }

   /**
    * Busca índices con coincidencia exacta en el conjunto de entrenamiento.
    */
   private fun findExactMatches(features: DoubleArray): List<Int> {
      return trainingFeatures.asSequence()
         .mapIndexedNotNull { index, row -> if (features.contentEquals(row)) index else null }
         .toList()
   }

   /**
    * Promedio de los targets de los índices provistos.
    */
   private fun averageTargetOf(indices: List<Int>): Double {
      return indices.asSequence().map { idx -> trainingTargets[idx] }.average()
   }

   /**
    * Similaridad coseno con todos los ejemplos, filtrando valores no positivos.
    */
   private fun cosineSimilarities(features: DoubleArray): List<Neighbor> {
      return trainingFeatures.asSequence()
         .mapIndexed { index, row -> Neighbor(index, cosineSimilarity(features, row)) }
         .filter { it.similarity > MIN_SIMILARITY_EPS }
         .sortedByDescending { it.similarity }
         .toList()
   }

   /**
    * Selecciona los K vecinos más cercanos respetando el tamaño disponible.
    */
   private fun topKNeighbors(similarities: List<Neighbor>): List<Neighbor> {
      val kEff = minOf(k, similarities.size)
      return similarities.take(kEff)
   }

   /**
    * Predicción ponderada por similaridad.
    */
   private fun weightedPrediction(neighbors: List<Neighbor>): Double {
      if (neighbors.isEmpty()) return DEFAULT_PREDICTION
      val weightedSum = neighbors.sumOf { (index, sim) -> trainingTargets[index] * sim }
      val weightSum = neighbors.sumOf { it.similarity }
      return if (weightSum > 0.0) weightedSum / weightSum else DEFAULT_PREDICTION
   }

   private fun cosineSimilarity(a: DoubleArray, b: DoubleArray): Double {
      if (a.size != b.size) return 0.0
      var dot = 0.0
      var normA = 0.0
      var normB = 0.0
      for (i in a.indices) {
         val x = a[i]
         val y = b[i]
         dot += x * y
         normA += x * x
         normB += y * y
      }
      val denom = sqrt(normA) * sqrt(normB)
      return if (denom > 0.0) dot / denom else 0.0
   }

   private data class Neighbor(val index: Int, val similarity: Double)
}
