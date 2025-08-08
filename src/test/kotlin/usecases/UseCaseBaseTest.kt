package textregressor.usecases

import textregressor.TextRegressor
import textregressor.TrainingSet
import kotlin.test.assertTrue

abstract class UseCaseBaseTest {

   abstract val trainingSet: TrainingSet

   fun trainModel(trainingSet: TrainingSet): TextRegressor {
      val model = TextRegressor()
      model.train(trainingSet)
      return model
   }

   fun assertInRange(
      model: TextRegressor,
      text: String,
      expectedRange: ClosedRange<Double>
   ) {
      val result = model.analyze(text)
      println("Texto: \"$text\"")
      println("Rango esperado: ${expectedRange.start}..${expectedRange.endInclusive})")
      println("Resultado: $result")

      assertTrue(result >= expectedRange.start && result <= expectedRange.endInclusive,
         "Fuera del rango esperado")
   }
}