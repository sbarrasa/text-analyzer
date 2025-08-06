package textregressor

import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.factory.Nd4j

object DataPreparer {
   fun vectorize(text: String, vocab: List<String>): DoubleArray {
      val words = text.lowercase().split(" ")
      return vocab.map { if (it in words) 1.0 else 0.0 }.toDoubleArray()
   }

   fun prepareDataset(examples: Map<String, Double>, vocab: List<String>): DataSet {
      val inputs = examples.keys.map { vectorize(it, vocab) }
      val labels = examples.values.map { doubleArrayOf(it) }
      val inputND = Nd4j.create(inputs.toTypedArray())
      val labelND = Nd4j.create(labels.toTypedArray())
      return DataSet(inputND, labelND)
   }
}

