package textregressor.usecases

import textregressor.Examples
import textregressor.TextRegressor
import kotlin.test.Test
import kotlin.test.assertTrue

class PredictUserReviewScoreTest {

   private val trainingData: Examples = mapOf(
      "excelente producto me encantó" to 5,
      "muy bueno y rápido" to 3,
      "me gustó bastante recomendable" to 2,
      "aceptable aunque algo lento" to 0.5,
      "ni bueno ni malo regular" to 0,
      "me decepcionó esperaba más" to -2,
      "malo no lo recomiendo" to -3,
      "pésimo experiencia horrible" to -6,
      "fue una compra buena" to 1,
      "no me convenció del todo" to -1,
      "desastre de producto" to -5
   )

   @Test
   fun neutralPositiveReviewTest() {
      val regressor = TextRegressor(epochs = 1000, learningRate = 0.01, hiddenSize = 8)
      regressor.train(trainingData)

      val testText = "el producto llegó rápido y funciona bien pero esperaba mejor calidad"
      val prediction = regressor.analyze(testText)

      println("Predicción para: \"$testText\" -> $prediction")

      assertTrue(prediction in 0.0..1.0)
   }

   @Test
   fun badReviewTest() {
      val regressor = TextRegressor(epochs = 300, learningRate = 0.01, hiddenSize = 8)
      regressor.train(trainingData)

      val testText = "El producto fue un desastre. Jamás volvería a tener una experiencia tan horrible como esta "
      val prediction = regressor.analyze(testText)

      println("Predicción para: \"$testText\" -> $prediction")

      assertTrue(prediction in -5.0..-3.0)
   }
}