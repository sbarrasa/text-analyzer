package textregressor

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.factory.Nd4j

typealias Examples = Map<String, Number>

class TextRegressor(
   private val epochs: Int = 100,
   private val learningRate: Double = 0.01,
   private val hiddenSize: Int = 10
) {
   private lateinit var vocab: List<String>
   private lateinit var model: MultiLayerNetwork

   fun train(examples: Examples) {
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

