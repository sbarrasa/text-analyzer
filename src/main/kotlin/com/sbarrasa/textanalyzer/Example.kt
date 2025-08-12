package com.sbarrasa.textanalyzer

data class Example(val text: String, val score: Double) {
    constructor(text: String, score: Number) : this(text, score.toDouble())
}

fun List<Example>.compute(): Double
   = this.map { it.score }.average().takeIf { !it.isNaN() } ?: 0.0
