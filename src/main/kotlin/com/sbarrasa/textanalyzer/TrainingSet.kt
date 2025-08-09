package com.sbarrasa.textanalyzer

typealias TrainingSet = Map<String, Number>

fun Collection<Number>.toDoubleArray(): DoubleArray = this.map { it.toDouble() }.toTypedArray().toDoubleArray()
