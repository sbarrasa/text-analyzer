package com.sbarrasa.textanalyzer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimpleTokenizerTest {

   @Test
   fun testEmptyInput() {
      val normalizer = object : Normalizer {
         override fun normalize(text: String): String = text
      }
      val tokenizer = SimpleTokenizer(normalizer)
      val result = tokenizer.tokenize("")
      assertTrue(result.isEmpty())
   }

   @Test
   fun testSingleWord() {
      val normalizer = object : Normalizer {
         override fun normalize(text: String): String = text
      }
      val tokenizer = SimpleTokenizer(normalizer)
      val result = tokenizer.tokenize("word")
      assertEquals(listOf("word"), result)
   }

   @Test
   fun testMultipleWordsWithWhitespace() {
      val normalizer = object : Normalizer {
         override fun normalize(text: String): String = text
      }
      val tokenizer = SimpleTokenizer(normalizer)
      val result = tokenizer.tokenize("   multiple   words with    spaces ")
      assertEquals(listOf("multiple", "words", "with", "spaces"), result)
   }

   @Test
   fun testNormalizedInput() {
      val normalizer = object : Normalizer {
         override fun normalize(text: String): String = text.uppercase()
      }
      val tokenizer = SimpleTokenizer(normalizer)
      val result = tokenizer.tokenize("mixed CASE words")
      assertEquals(listOf("MIXED", "CASE", "WORDS"), result)
   }

   @Test
   fun testCustomNormalizer() {
      val normalizer = object : Normalizer {
         override fun normalize(text: String): String = text.replace("remove", "")
      }
      val tokenizer = SimpleTokenizer(normalizer)
      val result = tokenizer.tokenize("remove this word from sentence")
      assertEquals(listOf("this", "word", "from", "sentence"), result)
   }
}