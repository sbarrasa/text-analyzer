package com.sbarrasa.textanalyzer

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.StringField
import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TermQuery

class LuceneExactMatcher : BaseLuceneEngine(), ExactMatcher {

   override fun train(samples: List<Example>) {
      trainWithExamples(samples)
   }

   override fun find(queryText: String): Double? {
      val searcher: IndexSearcher = searcherManager.acquire()
      return try {
         val exact = searcher.search(TermQuery(Term(FIELD_CONTENT_EXACT, queryText)), 1)
         if (exact.totalHits.value > 0L) {
            val doc = searcher.indexReader.storedFields().document(exact.scoreDocs[0].doc, setOf(FIELD_SCORE))
            doc.getField(FIELD_SCORE)?.numericValue()?.toDouble()
         } else null
      } catch (_: Exception) {
         null
      } finally {
         searcherManager.release(searcher)
      }
   }

   override fun buildDocument(text: String, score: Double): Document =
      Document().apply {
         add(StringField(FIELD_CONTENT_EXACT, text, Field.Store.NO))
         add(StoredField(FIELD_SCORE, score))
      }
}