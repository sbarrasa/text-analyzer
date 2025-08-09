package com.sbarrasa.textregressor

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.slf4j.LoggerFactory

class SpellCorrectorTest {
    
    private val log = LoggerFactory.getLogger(SpellCorrectorTest::class.java)
    
    @Test
    fun findBestMatchTest() {
        // Vocabulario de prueba
        val vocabulary = listOf("hello", "world", "kotlin", "programming", "test", "example")
        
        // Crear instancia del corrector ortográfico
        val spellCorrector = SpellCorrector(vocabulary)
        
        // Casos de prueba con resultados esperados
        val testCases = mapOf(
            "helo" to "hello",
            "wrold" to "world", 
            "kotln" to "kotlin",
            "programing" to "programming",
            "tst" to "test",
            "exampl" to "example",
            "hello" to "hello", // Ya es correcto
            "xyz" to "xyz"      // No hay coincidencia - debe devolver el original
        )
        
        log.debug("Testing SpellCorrector functionality:")
        testCases.forEach { (testWord, expected) ->
            val corrected = spellCorrector.findBestMatch(testWord)
            log.debug("'$testWord' -> '$corrected'")
            assertEquals(expected, corrected, "Failed to correct '$testWord' to '$expected'")
        }
    }
    
    @Test
    fun findBestMatchEmptyInputTest() {
        val vocabulary = listOf("hello", "world")
        val spellCorrector = SpellCorrector(vocabulary)
        
        assertEquals("", spellCorrector.findBestMatch(""))
        assertEquals("   ", spellCorrector.findBestMatch("   "))
    }
    
    @Test
    fun constructorConfigTest() {
        val vocabulary = listOf("hello", "world", "test")
        val config = SpellCorrector.Config(
            maxDistThreshold = 1,
            propDistDivisor = 3,
            lengthDiffTolerance = 1
        )
        val spellCorrector = SpellCorrector(vocabulary, config)
        
        // Con configuración más estricta, algunas correcciones podrían no funcionar
        val result = spellCorrector.findBestMatch("helo")
        log.debug("Strict config result for 'helo': $result")
        
        // Aún debería encontrar coincidencias exactas
        assertEquals("hello", spellCorrector.findBestMatch("hello"))
    }
}