package com.sbarrasa.textanalyzer

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.es.SpanishAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queries.mlt.MoreLikeThis
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.ByteBuffersDirectory
import java.io.StringReader
import kotlin.math.min

class TextAnalyzer {
   private companion object {
      const val FIELD_CONTENT = "content"
      const val FIELD_SCORE = "score"
      const val FIELD_ORIGINAL_TEXT = "original_text"
      const val MAX_RESULTS = 10
   }

   private val analyzer: Analyzer = SpanishAnalyzer()
   private val directory = ByteBuffersDirectory()
   private var indexSearcher: IndexSearcher? = null
   private var indexReader: DirectoryReader? = null

   private val trainingData = mutableMapOf<String, Double>()

   var isTrained = false
      private set

   fun train(examples: TrainingSet) {
      require(examples.isNotEmpty()) { "Training set cannot be empty" }

      trainingData.clear()
      examples.forEach { (text, score) -> trainingData[text] = score.toDouble() }

      createLuceneIndex()
      isTrained = true
   }

   fun analyze(text: String): Double {
      check(isTrained) { "Model must be trained before analysis." }
      require(text.isNotBlank()) { "Input text must not be blank." }

      return try {
         trainingData[text]?.let { return it }
         predictFromSimilarDocuments(text)
      } catch (_: Exception) {
         0.0
      }
   }

   private fun createLuceneIndex() {
      try {
         indexSearcher = null
         indexReader?.close()
         indexReader = null

         val config = IndexWriterConfig(analyzer)
         IndexWriter(directory, config).use { writer ->
            trainingData.forEach { (text, score) ->
               writer.addDocument(buildDocument(text, score))
            }
            writer.commit()
         }

         indexReader = DirectoryReader.open(directory)
         indexSearcher = IndexSearcher(indexReader)

      } catch (e: Exception) {
         throw RuntimeException("Failed to create Lucene index: ${e.message}", e)
      }
   }

   private fun predictFromSimilarDocuments(queryText: String): Double {
      val searcher = indexSearcher ?: return 0.0

      return try {
         val mlt = MoreLikeThis(searcher.indexReader).apply {
            analyzer = this@TextAnalyzer.analyzer
            fieldNames = arrayOf(FIELD_CONTENT)
            minTermFreq = 1
            minDocFreq = 1
            maxQueryTerms = 15
            minWordLen = 3
            maxWordLen = 50
         }

         val query = mlt.like(FIELD_CONTENT, StringReader(queryText))
         val limit = min(MAX_RESULTS, trainingData.size)
         val topDocs: TopDocs = searcher.search(query, limit)

         if (topDocs.scoreDocs.isEmpty()) {
            averageScore()
         } else {
            calculateWeightedPrediction(topDocs.scoreDocs, searcher)
         }
      } catch (_: Exception) {
         averageScore()
      }
   }

   private fun calculateWeightedPrediction(scoreDocs: Array<ScoreDoc>, searcher: IndexSearcher): Double {
      var weightedSum = 0.0
      var totalWeight = 0.0

      for (scoreDoc in scoreDocs) {
         try {
            val doc = searcher.doc(scoreDoc.doc)
            val score = doc.getField(FIELD_SCORE)?.numericValue()?.toDouble() ?: continue
            val weight = scoreDoc.score.toDouble()
            val adjustedWeight = weight * weight
            if (adjustedWeight > 0) {
               weightedSum += score * adjustedWeight
               totalWeight += adjustedWeight
            }
         } catch (_: Exception) {
            continue
         }
      }

      val result = if (totalWeight > 0.0) weightedSum / totalWeight else averageScore()

      val maxScore = scoreDocs.firstOrNull()?.score?.toDouble() ?: 0.0
      val scalingFactor = if (maxScore < 2.0) 0.8 else 1.0

      return result * scalingFactor
   }

   private fun buildDocument(text: String, score: Double): Document =
      Document().apply {
         add(TextField(FIELD_CONTENT, text, Field.Store.YES))
         add(StoredField(FIELD_SCORE, score))
         add(StoredField(FIELD_ORIGINAL_TEXT, text))
      }

   private fun averageScore(): Double =
      if (trainingData.isEmpty()) 0.0 else trainingData.values.average()


}

typealias TrainingSet = Map<String, Number>
