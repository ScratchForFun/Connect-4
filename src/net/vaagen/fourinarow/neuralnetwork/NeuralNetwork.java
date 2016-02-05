package net.vaagen.fourinarow.neuralnetwork;

import java.util.Random;

/**
 * Created by Magnus on 10/15/2015.
 */
public class NeuralNetwork {

    // Guide: http://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/

    // TODO : This is a triple layered Neural Network, one input, one hidden and one output layer.

    private float learningRate = 0.5F;

    private int inputNeurons;
    private int hiddenNeurons;
    private int outputNeurons;

    private Neuron[] inputLayer;
    private float[][] inputWeights;

    private Neuron[] hiddenLayer;
    private float[][] hiddenWeights;

    private Neuron[] outputLayer;

    public NeuralNetwork(int inputNeurons, int hiddenNeurons, int outputNeurons) {
        this.inputNeurons = inputNeurons;
        this.hiddenNeurons = hiddenNeurons;
        this.outputNeurons = outputNeurons;
    }

    public void trainNetwork(int echo) {
        for (int attempts = 0; attempts < echo; attempts++) {
            calculateHiddenLayers();
            calculateOutputLayers();

            float[] targetOutput = new float[outputNeurons]; // TODO : SET TARGET OUTPUT

            // Backwards propagation
            // Using partial derivatives
            float[][] newHiddenWeights = getNewHiddenWeights(new float[hiddenWeights.length][hiddenWeights[0].length], targetOutput);
            float[][] newInputWeights = getNewInputWeights(new float[inputWeights.length][inputWeights[0].length], targetOutput);

            hiddenWeights = newHiddenWeights;
            inputWeights = newInputWeights;

            if ((attempts+1) % 100 == 0) {
                //float totalError = calculateError();
                //System.out.println("The total error using Squared Error Function is " + totalError + " after attempt #" + (attempts+1));
            }
        }
    }

    public void initializeNetwork() {
        // Get Random
        Random random = new Random();

        // Input layer
        inputLayer = new Neuron[inputNeurons];
        for (int i = 0; i < inputLayer.length; i++)
            inputLayer[i] = new Neuron().setOutput(random.nextFloat());
        inputWeights = new float[inputNeurons][hiddenNeurons];
        for (int x = 0; x < inputWeights.length; x++)
            for (int y = 0; y < inputWeights[0].length; y++)
                inputWeights[x][y] = random.nextFloat();

        // Hidden layer
        hiddenLayer = new Neuron[hiddenNeurons];
        for (int i = 0; i < hiddenLayer.length; i++)
            hiddenLayer[i] = new Neuron().setBias(random.nextFloat());
        hiddenWeights = new float[hiddenNeurons][outputNeurons];
        for (int x = 0; x < hiddenWeights.length; x++)
            for (int y = 0; y < hiddenWeights[0].length; y++)
                hiddenWeights[x][y] = random.nextFloat();

        // Output layer
        outputLayer = new Neuron[outputNeurons];
        for (int i = 0; i < outputLayer.length; i++)
            outputLayer[i] = new Neuron().setBias(random.nextFloat()); //.setTargetOutput(random.nextFloat());
    }

    public void mutate() {
        Random random = new Random();
        int layer = random.nextInt(2); // The input weights or the hidden weights
        if (layer == 0) {
            inputWeights[random.nextInt(inputWeights.length)][random.nextInt(inputWeights[0].length)] += random.nextFloat() - 0.5F;
        } else {
            hiddenWeights[random.nextInt(hiddenWeights.length)][random.nextInt(hiddenWeights[0].length)] += random.nextFloat() - 0.5F;
        }
    }

    public float[] calculateOutput() {
        calculateHiddenLayers();
        calculateOutputLayers();

        float[] ouput = new float[outputLayer.length];
        for (int i = 0; i < outputLayer.length; i++)
            ouput[i] = outputLayer[i].getOutput();

        return ouput;
    }

    public void setInput(int inputNeuron, float value) {
        inputLayer[inputNeuron].setOutput(value);
    }

    private void calculateHiddenLayers() {
        // Calculate hidden layers
        // Loop through the hidden layer
        for (int h = 0; h < hiddenLayer.length; h++) {
            Neuron hiddenNeuron = hiddenLayer[h];
            float netInput = 0;

            // Loop through the input layer
            for (int i = 0; i < inputLayer.length; i++) {
                Neuron inputNeuron = inputLayer[i];
                netInput += inputWeights[i][h] * inputNeuron.getOutput(); // Weight * Input
            }

            // Add the bias to the netInput
            netInput += hiddenNeuron.getBias();

            //System.out.println("Setting netInput for hidden neuron #" + h + " to " + netInput);
            hiddenNeuron.setInput(netInput);
            hiddenNeuron.calculateOutput();
            //System.out.println("Output from hidden neuron #" + h + " was '" + hiddenNeuron.getOutput() + "'");
        }
    }

    private void calculateOutputLayers() {
        // Calculate output
        // Loop through the output layer
        for (int h = 0; h < outputLayer.length; h++) {
            Neuron outputNeurons = outputLayer[h];
            float netInput = 0;

            // Loop through the hidden layer
            for (int i = 0; i < hiddenLayer.length; i++) {
                Neuron hiddenNeuron = hiddenLayer[i];
                netInput += hiddenWeights[i][h] * hiddenNeuron.getOutput(); // Weight * Input
            }

            // Add the bias to the netInput
            netInput += outputNeurons.getBias();

            //System.out.println("Setting netInput for output neuron #" + h + " to " + netInput);
            outputNeurons.setInput(netInput);
            outputNeurons.calculateOutput();
            //System.out.println("Output from output neuron #" + h + " was '" + outputNeurons.getOutput() + "'");
        }
    }

    private float calculateError(float[] targetOutput) {
        // Calculate the error using Squared Error Function
        float totalError = 0;
        for (int o = 0; o < outputLayer.length; o++) {
            Neuron outputNeuron = outputLayer[o];
            float error = (float) (Math.pow(targetOutput[o] - outputNeuron.getOutput(), 2) / 2);
            System.out.println("Output Neuron #" + o + ":  Target output: [" + targetOutput[o] + "], Output: [" + outputNeuron.getOutput() + "], Error: [" + error + "]");
            totalError += error;
        }

        return totalError;
    }

    private float[][] getNewHiddenWeights(float[][] newHiddenWeights, float[] targetOutput) {
        // Loop through the output layer
        for (int o = 0; o < outputLayer.length; o++) {
            Neuron outputNeuron = outputLayer[o];

            // Loop through the hidden layer
            for (int h = 0; h < hiddenLayer.length; h++) {
                // We want to know how much a change in the output's first weight affects the total error
                // For this we use the chain rule

                // Partial-ErrorTotal divided by Partial-Output
                float f1 = -(targetOutput[o] - outputNeuron.getOutput());
                float f2 = outputNeuron.getOutput() * (1 - outputNeuron.getOutput());
                float f3 = hiddenLayer[h].getOutput();

                // Can also be combined in the form of the delta rule
                float totalErrorChange = f1 * f2 * f3;
                newHiddenWeights[h][o] = hiddenWeights[h][o] - totalErrorChange * learningRate;
            }
        }

        return newHiddenWeights;
    }

    private float[][] getNewInputWeights(float[][] newInputWeights, float[] targetOutput) {
        for (int h = 0; h < hiddenLayer.length; h++) {
            for (int i = 0; i < inputLayer.length; i++) {
                Neuron hiddenNeuron = hiddenLayer[h];

                // Calculate for f1
                float f1 = 0;
                // Error for output neurons
                for (int o = 0; o < outputLayer.length; o++) {
                    Neuron outputNeuron = outputLayer[o];
                    float f1111 = -(targetOutput[o] - outputNeuron.getOutput()); // Error-01 / Out-O1
                    float f1112 = outputNeuron.getOutput() * (1 - outputNeuron.getOutput()); // Out-O1 / Net-O1

                    float f111 = f1111 * f1112; // Error-O1 / Net-O1
                    float f112 = hiddenWeights[h][o]; // Net-O1 / Out-H1

                    f1 += f111 * f112; // Error-O1 / Out-H1
                }

                float f2 = hiddenNeuron.getOutput() * (1 - hiddenNeuron.getOutput()); // Out-H1 / Net-H1
                float f3 = inputLayer[i].getOutput(); // Net-H1 / W1 = Input1

                float totalErrorChange = f1 * f2 * f3;
                newInputWeights[i][h] = inputWeights[i][h] - totalErrorChange * learningRate;

                //System.out.println("New weight equals " + newInputWeights[i][h]);
            }
        }

        return newInputWeights;
    }

    public NeuralNetwork clone() {
        NeuralNetwork network = new NeuralNetwork(inputNeurons, hiddenNeurons, outputNeurons);
        network.learningRate = learningRate;
        network.inputLayer = inputLayer.clone();
        network.inputWeights = deepCloneArray(inputWeights);
        network.hiddenLayer = hiddenLayer.clone();
        network.hiddenWeights = deepCloneArray(hiddenWeights);
        network.outputLayer = outputLayer.clone();
        return network;
    }

    public NeuralNetwork crossBreed(NeuralNetwork network) {
        NeuralNetwork neuralNetwork = clone();

        for (int x = 0; x < inputWeights.length; x++) {
            for (int y = 0; y < inputWeights[0].length; y++) {
                if (new Random().nextInt(2) == 0)
                    neuralNetwork.inputWeights[x][y] = network.inputWeights[x][y];
            }
        }

        for (int x = 0; x < hiddenWeights.length; x++) {
            for (int y = 0; y < hiddenWeights[0].length; y++) {
                if (new Random().nextInt(2) == 0)
                    neuralNetwork.hiddenWeights[x][y] = network.hiddenWeights[x][y];
            }
        }

        return neuralNetwork;
    }

    public float[][] deepCloneArray(float[][] array) {
        if (array == null)
            return null;
        float[][] result = new float[array.length][];
        for (int r = 0; r < array.length; r++)
            result[r] = array[r].clone();
        return result;
    }

}
