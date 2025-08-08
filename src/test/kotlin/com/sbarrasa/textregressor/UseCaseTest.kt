package com.sbarrasa.textregressor

import com.sbarrasa.textregressor.TextRegressor
import com.sbarrasa.textregressor.TrainingSet
import mu.KotlinLogging
import kotlin.test.assertTrue

abstract class UseCaseTest {

   private val logger = KotlinLogging.logger {}

   abstract val trainingSet: TrainingSet

   val model: TextRegressor by lazy { TextRegressor(trainingSet) }

   fun <T> assertInRange(
      text: String,
      expectedRange: ClosedRange<T>
   ) where T : Number, T : Comparable<T> {
      val result = model.analyze(text)
      logger.info("Texto: \"$text\"")
      logger.info("esperado: ${expectedRange.start}..${expectedRange.endInclusive} Resultado: $result")

      assertTrue(
         result >= expectedRange.start.toDouble() && result <= expectedRange.endInclusive.toDouble(),
         "Fuera del rango esperado"
      )
   }
}