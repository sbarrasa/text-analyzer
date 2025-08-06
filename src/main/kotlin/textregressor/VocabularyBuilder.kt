package textregressor

object VocabularyBuilder {
   fun build(examples: Map<String, Double>): List<String> =
      examples.keys.flatMap { it.lowercase().split(" ") }.toSet().toList()
}

