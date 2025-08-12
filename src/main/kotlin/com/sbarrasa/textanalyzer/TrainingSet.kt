package com.sbarrasa.textanalyzer

typealias TrainingSet = Map<String, Number>

fun TrainingSet.toExampleList(): List<Example>
         = this.map { Example(it.key, it.value) }