package textregressor

import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.Adam
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.nd4j.linalg.dataset.DataSet

object ModelTrainer {
   fun trainModel(
      dataset: DataSet,
      inputSize: Int,
      epochs: Int,
      learningRate: Double,
      hiddenSize: Int
   ): MultiLayerNetwork {
      val config = NeuralNetConfiguration.Builder()
         .updater(Adam(learningRate))
         .list()
         .layer(
            DenseLayer.Builder()
               .nIn(inputSize)
               .nOut(hiddenSize)
               .activation(Activation.RELU)
               .build()
         )
         .layer(
            OutputLayer.Builder()
               .nIn(hiddenSize)
               .nOut(1)
               .activation(Activation.IDENTITY)
               .lossFunction(LossFunctions.LossFunction.MSE)
               .build()
         )
         .build()

      val model = MultiLayerNetwork(config)
      model.init()

      repeat(epochs) { model.fit(dataset) }

      return model
   }
}
