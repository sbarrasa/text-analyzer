package com.sbarrasa.textanalyzer

fun main() {
    println("Testing SimpleNgramGenerator...")
    val generator = SimpleNgramGenerator()
    
    // Test case from generateUnigramsTest
    val tokens = listOf("hello", "world", "test")
    val result = generator.generate(tokens, 1..1)
    val expected = listOf("hello", "world", "test")
    
    println("Tokens: $tokens")
    println("Range: 1..1")
    println("Expected: $expected")
    println("Actual: $result")
    println("Match: ${expected == result}")
    
    // Test case from generateBigramsTest
    val tokens2 = listOf("hello", "world", "test", "case")
    val result2 = generator.generate(tokens2, 2..2)
    val expected2 = listOf("hello world", "world test", "test case")
    
    println("\nTokens: $tokens2")
    println("Range: 2..2")
    println("Expected: $expected2")
    println("Actual: $result2")
    println("Match: ${expected2 == result2}")
}