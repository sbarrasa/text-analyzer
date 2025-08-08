package textregressor.usecases

import textregressor.TextRegressor
import textregressor.TrainingSet
import kotlin.test.Test

class ProductSatisfactionTest : UseCaseBaseTest() {

   override val trainingSet: TrainingSet = mapOf(
      "una porquería" to -5,
      "un desastre" to -5,
      "horrible" to -4,
      "mala calidad" to -4,
      "no recomendable" to -3,
      "defectuoso" to -3,
      "me arrepiento" to -3,
      "pésimo" to -4,
      "falló rápido" to -3,
      "cumple" to 0,
      "aceptable" to 0,
      "normal" to 0.2,
      "regular" to 0.1,
      "ni fu ni fa" to 0,
      "me encantó" to 5,
      "excelente" to 5,
      "perfecto" to 4,
      "muy bueno" to 4,
      "funciona bien" to 4,
      "recomendable" to 4,
      "vale la pena" to 4
   )

   @Test
   fun neutralEvaluationShort() {
      val text = "El producto cumple lo que promete, nada fuera de lo común."
      val model = trainModel(trainingSet)
      assertInRange(model, text, (-5.0)..(-4.0))
   }

   @Test
   fun positiveEvaluationShort() {
      val text = "Me encantó, funciona perfecto y vale totalmente la pena."
      val model = trainModel(trainingSet)
      assertInRange(model, text, 0.5..1.0)
   }

   @Test
   fun negativeEvaluationShort() {
      val text = "Una porquería, pésimo y defectuoso desde el primer día."
      val model = trainModel(trainingSet)
      assertInRange(model, text, (-5.0)..(-3.0))
   }
}
