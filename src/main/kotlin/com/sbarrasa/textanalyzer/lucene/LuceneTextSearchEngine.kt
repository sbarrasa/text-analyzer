package com.sbarrasa.textanalyzer.lucene

import com.sbarrasa.textanalyzer.Neighbor
import com.sbarrasa.textanalyzer.TextSearchEngine
import com.sbarrasa.textanalyzer.TrainingSet
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.CharArraySet
import org.apache.lucene.analysis.es.SpanishAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queries.mlt.MoreLikeThis
import org.apache.lucene.search.SearcherManager
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.similarities.Similarity
import org.apache.lucene.store.ByteBuffersDirectory
import org.apache.lucene.store.Directory
import java.io.StringReader
import kotlin.math.min

class LuceneTextSearchEngine(
   private val analyzer: Analyzer = SpanishAnalyzer(CharArraySet.EMPTY_SET),
   private val directory: Directory = ByteBuffersDirectory(),
   indexWriterConfig: IndexWriterConfig = IndexWriterConfig(analyzer),
   similarity: Similarity? = null,
   private val fieldContent: String = "content",
   private val fieldExact: String = "content_exact",
   private val fieldScore: String = "score"
) : TextSearchEngine, AutoCloseable {

   private val writer = IndexWriter(directory, indexWriterConfig.also { cfg ->
      if (similarity != null) cfg.similarity = similarity
   })
   private val manager = SearcherManager(writer, null)

   private var avgScore: Double = 0.0

   override fun train(trainingSet: TrainingSet) {
      avgScore = trainingSet.values.map { it.toDouble() }
         .average()
         .takeIf { !it.isNaN() } ?: 0.0

      writer.deleteAll()
      for ((text, score) in trainingSet) {
         writer.addDocument(
            Document().apply {
               add(TextField(fieldContent, text, Field.Store.NO))
               add(StringField(fieldExact, text, Field.Store.NO))
               add(StoredField(fieldScore, score.toDouble()))
            }
         )
      }
      writer.commit()
      manager.maybeRefreshBlocking()
   }

   override fun defaultScore(): Double = avgScore

   override fun findExact(queryText: String): Double? {
      val searcher = manager.acquire()
      return try {
         val top = searcher.search(TermQuery(Term(fieldExact, queryText)), 1)
         if (top.totalHits.value == 0L) null
         else {
            val docId = top.scoreDocs[0].doc
            val doc = searcher.indexReader.storedFields().document(docId, setOf(fieldScore))
            doc.getField(fieldScore)?.numericValue()?.toDouble()
         }
      } catch (_: Exception) {
         null
      } finally {
         manager.release(searcher)
      }
   }

   override fun find(queryText: String, k: Int): List<Neighbor> {
      val searcher = manager.acquire()
      return try {
         val mlt = MoreLikeThis(searcher.indexReader).apply {
            analyzer = this@LuceneTextSearchEngine.analyzer
            fieldNames = arrayOf(fieldContent)
            minTermFreq = 1
            minDocFreq = 1
            maxQueryTerms = 15
            minWordLen = 3
            maxWordLen = 50
         }
         val query = mlt.like(fieldContent, StringReader(queryText))
         val limit = min(k, searcher.indexReader.numDocs())
         val top = searcher.search(query, limit)

         top.scoreDocs.mapNotNull { sd ->
            val doc = searcher.indexReader.storedFields().document(sd.doc, setOf(fieldScore))
            val s = doc.getField(fieldScore)?.numericValue()?.toDouble() ?: return@mapNotNull null
            Neighbor(score = s, weight = sd.score.toDouble())
         }
      } catch (_: Exception) {
         emptyList()
      } finally {
         manager.release(searcher)
      }
   }

   override fun close() {
      manager.close()
      writer.close()
      directory.close()
   }
}