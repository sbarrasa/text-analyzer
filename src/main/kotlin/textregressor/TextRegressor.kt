package textregressor

import org.deeplearning4j.nn.multilayer.*
import org.nd4j.linalg.factory.*

class TextRegressor(
   private val epochs: Int = 1000,
   private val learningRate: Double = 0.01,
   private val hiddenSize: Int = 10
) {
   private lateinit var vocab: List<String>
   private lateinit var model: MultiLayerNetwork

   fun train(examples: Map<String, Double>) {
      vocab = VocabularyBuilder.build(examples)
      val dataset = DataPreparer.prepareDataset(examples, vocab)
      model = ModelTrainer.trainModel(dataset, vocab.size, epochs, learningRate, hiddenSize)
   }

   fun analyze(text: String): Double {
      val vector = DataPreparer.vectorize(text, vocab)
      val input = Nd4j.create(vector).reshape(intArrayOf(1, vocab.size))
      return model.output(input).getDouble(0)
   }
}

