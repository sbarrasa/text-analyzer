package textregressor

object VocabularyBuilder {
   fun build(examples: Examples): List<String> =
      examples.keys.flatMap { it.lowercase().split(" ") }.toSet().toList()
}

