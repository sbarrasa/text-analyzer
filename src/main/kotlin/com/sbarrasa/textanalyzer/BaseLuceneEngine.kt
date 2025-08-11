package com.sbarrasa.textanalyzer

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.es.SpanishAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.SearcherManager
import org.apache.lucene.store.ByteBuffersDirectory

abstract class BaseLuceneEngine : AutoCloseable {
    protected companion object {
        const val FIELD_CONTENT = "content"
        const val FIELD_CONTENT_EXACT = "content_exact"
        const val FIELD_SCORE = "score"
    }

    protected val analyzer: Analyzer = SpanishAnalyzer()
    protected val directory = ByteBuffersDirectory()
    protected val indexWriter = IndexWriter(directory, IndexWriterConfig(analyzer))
    protected val searcherManager = SearcherManager(indexWriter, null)

    protected fun trainWithExamples(examples: List<Example>) {
        indexWriter.deleteAll()
        examples.forEach { sample ->
            indexWriter.addDocument(buildDocument(sample.text, sample.score))
        }
        indexWriter.commit()
        searcherManager.maybeRefreshBlocking()
    }

    protected fun readScore(searcher: IndexSearcher, sd: ScoreDoc): Double {
        val doc = searcher.indexReader.storedFields().document(sd.doc, setOf(FIELD_SCORE))
        return doc.getField(FIELD_SCORE)?.numericValue()?.toDouble() ?: 0.0
    }

    protected open fun buildDocument(text: String, score: Double): Document =
        Document().apply {
            add(TextField(FIELD_CONTENT, text, Field.Store.NO))
            add(StringField(FIELD_CONTENT_EXACT, text, Field.Store.NO))
            add(StoredField(FIELD_SCORE, score))
        }

    override fun close() {
        searcherManager.close()
        indexWriter.close()
        directory.close()
    }
}