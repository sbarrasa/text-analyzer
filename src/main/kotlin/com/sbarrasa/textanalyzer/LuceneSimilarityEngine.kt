package com.sbarrasa.textanalyzer

import org.apache.lucene.queries.mlt.MoreLikeThis
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TopDocs
import java.io.StringReader
import kotlin.math.min

class LuceneSimilarityEngine : BaseLuceneEngine(), SimilarityEngine {

   override fun train(examples: List<Example>) {
      trainWithExamples(examples)
   }

   override fun find(queryText: String, k: Int): List<Neighbor> {
      val searcher: IndexSearcher = searcherManager.acquire()
      return try {
         val mlt = MoreLikeThis(searcher.indexReader).apply {
            analyzer = this@LuceneSimilarityEngine.analyzer
            fieldNames = arrayOf(FIELD_CONTENT)
            minTermFreq = 1
            minDocFreq = 1
            maxQueryTerms = 15
            minWordLen = 3
            maxWordLen = 50
         }

         val query = mlt.like(FIELD_CONTENT, StringReader(queryText))
         val limit = min(k, searcher.indexReader.numDocs())
         val topDocs: TopDocs = searcher.search(query, limit)

         topDocs.scoreDocs.map { sd ->
            val score = readScore(searcher, sd)
            val weight = sd.score.toDouble()
            Neighbor(score, weight)
         }
      } catch (_: Exception) {
         emptyList()
      } finally {
         searcherManager.release(searcher)
      }
   }

}