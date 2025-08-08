package textregressor.usecases

import textregressor.*
import kotlin.test.Test

class PriorityDetectionTest : UseCaseBaseTest() {


   override val trainingSet: TrainingSet = mapOf(
      "sin urgencia" to 0,
      "cuando puedan" to 0.5,
      "puede esperar" to 1,
      "prioridad baja" to 1,
      "no urgente" to 0,
      "cuando sea posible" to 0.5,
      "moderada prioridad" to 2,
      "atención necesaria" to 3,
      "es importante" to 3.5,
      "se necesita pronto" to 4,
      "urgente" to 5,
      "prioridad máxima" to 5,
      "requiere acción inmediata" to 5,
      "por favor resolver hoy" to 5,
      "necesita respuesta rápida" to 4.5,
      "problema crítico" to 5,
      "no puede esperar más" to 5,
      "es fundamental" to 4,
      "alta prioridad" to 4.5,
      "revisar cuanto antes" to 4
   )

   @Test
   fun lowPriority() {
      val text = """Buenas tardes:
         Quisiera consultar sobre el estado de la factura del mes pasado. No es urgente, 
         puede responder cuando tenga tiempo. Muchas gracias.
        """

      val model = trainModel(trainingSet)
      assertInRange(model, text, 0.0..2.0)
   }

   @Test
   fun mediumPriority() {
      val text = """
            Hola:
            Necesitamos que revisen el informe que enviamos ayer para poder avanzar con el proyecto. 
            Es importante pero no crítico. Agradezco que lo puedan ver en las próximas 48 horas.
        """

      val model = trainModel(trainingSet)
      assertInRange(model, text, 2.0..3.5)
   }

   @Test
   fun highPriority() {
      val text = """
            Atención urgente:
            El sistema principal está caído y está afectando a todos los usuarios. 
            Necesitamos que se solucione de inmediato para evitar pérdidas. 
            Por favor, responder con la máxima prioridad.
        """

      val model = trainModel(trainingSet)
      assertInRange(model, text, 2.5..3.0)
   }
}
