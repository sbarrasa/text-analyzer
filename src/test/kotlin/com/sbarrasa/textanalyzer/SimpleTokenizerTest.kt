package com.sbarrasa.textanalyzer

class SimpleTokenizer {
   private companion object {
      val WHITESPACE_REGEX = "\\s+".toRegex()
   }

   fun split(text: String): Array<String> {
       if (text.isBlank()) return emptyArray()
       return text.trim().split(WHITESPACE_REGEX).toTypedArray()
   }
}
