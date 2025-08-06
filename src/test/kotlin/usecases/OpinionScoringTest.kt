import textregressor.TextRegressor
import kotlin.test.Test
import kotlin.test.assertTrue

class OpinionScoringTest {

   @Test
   fun `detecta grado de positividad y negatividad`() {
      val model = TextRegressor()
      val training = mapOf(
         "horrible experiencia" to -5.0,
         "muy malo" to -4.0,
         "no me gustó" to -2.0,
         "es aceptable" to 0.0,
         "me gustó bastante" to 2.5,
         "muy bueno" to 4.0,
         "excelente, lo recomiendo" to 5.0
      )
      model.train(training)

      val resultPos = model.analyze("excelente producto, muy bueno")
      val resultNeg = model.analyze("malo, no me gustó")
      val resultNeutral = model.analyze("aceptable")

      println("Positivo: $resultPos")
      println("Negativo: $resultNeg")
      println("Neutro: $resultNeutral")

      assertTrue(resultPos > 3.0)
      assertTrue(resultNeg < -1.0)
      assertTrue(resultNeutral in -1.0..1.0)
   }
}
