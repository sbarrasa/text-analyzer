package com.sbarrasa.textanalyzer

class VocabularyExtractor {
   private val tokenizer = SimpleTokenizer()

   companion object {
      private const val DEFAULT_MAX_VOCAB_SIZE = 100
   }

   fun buildVocabulary(texts: Collection<String>, maxSize: Int = DEFAULT_MAX_VOCAB_SIZE): List<String> {
      if (texts.isEmpty()) return emptyList()

      val frequency = mutableMapOf<String, Int>()

      texts.forEach { text ->
         val tokens = tokenizer.split(text)
         countUnigrams(tokens, frequency)
         countBigrams(tokens, frequency)
      }

      return frequency.entries
         .sortedByDescending { it.value }
         .take(maxSize.coerceAtLeast(0))
         .map { it.key }
         .sorted()
   }

   private fun countUnigrams(tokens: Array<String>, frequency: MutableMap<String, Int>) {
      tokens.forEach { token ->
         frequency[token] = (frequency[token] ?: 0) + 1
      }
   }

   private fun countBigrams(tokens: Array<String>, frequency: MutableMap<String, Int>) {
      tokens.asList().windowed(size = 2, step = 1, partialWindows = false).forEach { (a, b) ->
         val bigram = "$a $b"
         frequency[bigram] = (frequency[bigram] ?: 0) + 1
      }
   }
}
