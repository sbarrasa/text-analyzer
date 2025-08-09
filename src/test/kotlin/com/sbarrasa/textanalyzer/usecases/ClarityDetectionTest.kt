package com.sbarrasa.textanalyzer.usecases

import com.sbarrasa.textanalyzer.TrainingSet
import com.sbarrasa.textanalyzer.UseCaseTest
import kotlin.test.Test


class ClarityDetectionTest : UseCaseTest() {


   override val trainingSet: TrainingSet = mapOf(
      "puede que sea así o no" to 0,
      "es posible que haya errores" to 0,
      "no estoy seguro de los resultados" to 0,
      "tal vez se debería revisar" to 0,
      "podría ser que funcione" to 0,
      "es complicado de decir" to 0,
      "el sistema falla al iniciar sesión" to 1,
      "el informe muestra los datos correctos" to 1,
      "la conexión se pierde a las dos horas" to 1,
      "la configuración está mal hecha" to 1,
      "el error se reproduce en todos los casos" to 1,
      "la función retorna un valor nulo" to 1,
      "los pasos para reproducir el fallo son..." to 1,
      "se debe actualizar la base de datos" to 1,
      "el problema ocurre solo en la versión 3" to 1
   )

   @Test
   fun ambiguousTextLong() {
      val text = """
            Estoy revisando el reporte que recibimos y, aunque parece que hay algunos errores,
            no puedo asegurar con certeza cuáles son los problemas específicos ni su origen. 
            Hay varias posibles causas que podrían estar afectando el funcionamiento, 
            pero sin un análisis más detallado no es posible determinar exactamente qué falla. 
            Podría tratarse de un problema en la configuración, o tal vez sea un error en la versión actual, 
            aunque también existe la posibilidad de que algún componente externo esté interfiriendo.
        """

      assertInRange(text, 0.0..0.7)
   }


   @Test
   fun clearTextLong() {
      val text = """
            Se ha detectado que el sistema falla al iniciar sesión con usuarios de la versión actual. 
            Cada intento de acceso devuelve un código de error 403 y se ha confirmado que el problema 
            ocurre en todas las cuentas activas. Esto impide que los usuarios puedan utilizar la plataforma, 
            generando una interrupción crítica en el servicio. Se recomienda realizar una actualización urgente 
            en el módulo de autenticación para resolver este fallo y restablecer el acceso a la brevedad.
        """

      assertInRange(text, 0.8..1.0)
   }
}
