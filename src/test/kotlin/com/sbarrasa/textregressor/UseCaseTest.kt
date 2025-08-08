package com.sbarrasa.textregressor

import mu.KotlinLogging
import kotlin.test.assertTrue

abstract class UseCaseTest {

   private val logger = KotlinLogging.logger {}

   abstract val trainingSet: TrainingSet

   val model: TextRegressor by lazy { TextRegressor(trainingSet) }

   fun assertInRange(
      text: String,
      expectedRange: ClosedRange<Double>
   ) {
      val result = model.analyze(text)
      logger.info("Texto: \"$text\"")
      logger.info("Esperado: $expectedRange | Resultado: $result")

      assertTrue(
         result in expectedRange,
         "Fuera del rango esperado"
      )
   }
}