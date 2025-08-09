package com.sbarrasa.textregressor.usecases

import com.sbarrasa.textregressor.UseCaseTest
import kotlin.test.Test


class NewsObjectivityTest : UseCaseTest() {

   override val trainingSet = mapOf(
         "El informe detalla que la inflación alcanzó un 6% en junio, según datos del INDEC." to 0.1,
         "La Organización Mundial de la Salud reportó un aumento global en casos de dengue." to 0.1,
         "El nuevo reglamento fue aprobado con 48 votos a favor y 22 en contra, informó el Senado." to 0.1,
         "El índice de desempleo se mantuvo en 7,2%, de acuerdo con el informe trimestral del Ministerio." to 0.1,
         "El clima se mantendrá estable durante la semana, según el Servicio Meteorológico Nacional." to 0.1,
         "Las exportaciones aumentaron un 12% respecto al mismo mes del año anterior." to 0.1,
         "Los estudiantes secundarios participaron en la feria de ciencia con más de 60 proyectos." to 0.1,
         "El acuerdo comercial fue firmado el martes por los representantes de ambas naciones." to 0.1,
         "La ley fue publicada en el boletín oficial el miércoles por la mañana." to 0.1,
         "El servicio de trenes se reanudó tras cuatro horas de interrupción por fallas técnicas." to 0.1,

         "El gobierno volvió a fallar en su promesa de bajar la inflación." to 0.9,
         "Es vergonzoso que los legisladores no hayan debatido este tema antes de aprobarlo." to 0.95,
         "Este informe intenta ocultar la verdadera causa del desempleo." to 0.9,
         "Claramente el presidente no tiene idea de cómo manejar la crisis económica." to 0.95,
         "El artículo parece más una propaganda oficial que una nota informativa." to 0.9,
         "Resulta indignante que no se tomen medidas urgentes ante la situación actual." to 0.95,
         "Lo que sucede con el sistema de salud es un verdadero desastre anunciado." to 0.9,
         "Otra vez los medios ocultan lo que en realidad está pasando con los salarios." to 0.9,
         "La oposición simplemente busca boicotear cualquier medida sin fundamentos." to 0.9,
         "Los jueces actúan como si estuvieran por encima de la ley." to 0.9
      )

   @Test
   fun neutralArticle() {
      val text = """
         Según datos publicados por el Instituto Nacional de Estadísticas, el país registró un crecimiento económico del 3,4% en el segundo trimestre del año.
         El sector de la construcción fue uno de los principales impulsores, con un incremento del 7% respecto al trimestre anterior.
         El informe también destacó un leve aumento en el consumo privado, impulsado por las políticas de incentivo aplicadas en abril.
         Las exportaciones, en cambio, sufrieron una leve caída del 1,2% por la disminución de demanda en los mercados internacionales.
         Las autoridades señalaron que la tendencia positiva podría sostenerse si se mantiene la estabilidad cambiaria.
     """

      assertInRange(text, 0.0..0.6)
   }

   @Test
   fun subjectiveArticle() {
      val text = """
         El reciente informe del gobierno intenta mostrar un crecimiento económico que en realidad no se percibe en las calles.
         Aunque se mencionan cifras positivas, la gente sigue sin llegar a fin de mes y los salarios continúan congelados.
         Es evidente que las estadísticas fueron presentadas con un claro sesgo optimista para beneficiar la imagen oficialista.
         El relato de una recuperación económica no se corresponde con la realidad de millones de ciudadanos.
         Resulta cada vez más claro que estas noticias buscan encubrir los verdaderos problemas estructurales del país.
     """

      assertInRange(text, 0.3..1.0)
   }
}
