package com.sbarrasa.textanalyzer

class SimpleTokenizer(){
    fun split(text: String): Array<String> {
        if (text.isEmpty()) return emptyArray()
        
        return text.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .toTypedArray()
    }
}
