package com.sbarrasa.textanalyzer

fun main() {
    println("Testing BasicNormalizer punctuation...")
    val normalizer = BasicNormalizer(stripPunctuation = true, collapseWhitespace = false, trimWhitespace = false)
    
    val testCases = mapOf(
        "Hello, World!" to "hello  world ",
        "Test: this-is a test." to "test  this is a test ",
        "No punctuation here" to "no punctuation here",
        "!@#$%^&*()" to "         "
    )
    
    testCases.forEach { (input, expected) ->
        val result = normalizer.normalize(input)
        println("Input: '$input'")
        println("Expected: '$expected'")
        println("Actual: '$result'")
        println("Match: ${expected == result}")
        println("Expected length: ${expected.length}")
        println("Actual length: ${result.length}")
        println()
    }
}