package net.vaagen.fourinarow.neuralnetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by Magnus on 10/15/2015.
 */
public class NeuralNetwork {

    // Guide: http://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/

    // TODO : This is a triple layered Neural Network, one input, one hidden and one output layer.

    private float learningRate = 0.4F;

    private int inputNeurons;
    private int hiddenNeurons;
    private int outputNeurons;

    private Neuron[] inputLayer;
    private float[][] inputWeights;

    private Neuron[] hiddenLayer;
    private float[][] hiddenWeights;

    private Neuron[] outputLayer;
    
    private File savedNetwork;
    private int correctPredictions;
    private int totalPredictions;

    public NeuralNetwork(int inputNeurons, int hiddenNeurons, int outputNeurons) {
        this.inputNeurons = inputNeurons;
        this.hiddenNeurons = hiddenNeurons;
        this.outputNeurons = outputNeurons;
    }
    
    public void setSavedFile(File file) {
    	this.savedNetwork = file;
    }

    public void trainNetwork(float[] expectedOutput) {
        calculateHiddenLayers();
        calculateOutputLayers();
        
        // Backwards propagation
        // Using partial derivatives
        float[][] newHiddenWeights = getNewHiddenWeights(new float[hiddenWeights.length][hiddenWeights[0].length], expectedOutput);
        float[][] newInputWeights = getNewInputWeights(new float[inputWeights.length][inputWeights[0].length], expectedOutput);

        hiddenWeights = newHiddenWeights;
        inputWeights = newInputWeights;
    }

    public void initializeNetwork() {
    	if (savedNetwork == null || !savedNetwork.exists()) {
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
    	} else {
    		try {
	    		// FileReader reads text files in the default encoding.
	            FileReader fileReader = new FileReader(savedNetwork);
	            // Always wrap FileReader in BufferedReader.
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
	            String input = "";
	            while ((input = bufferedReader.readLine()) != null) {
	            	String[] inputArray = input.split(": ");
	            	if (inputArray[0].equalsIgnoreCase("learning rate")) {
	            		try {
	            			float learningRate = Float.parseFloat(inputArray[0]);
	            			setLearningRate(learningRate);
	            		} catch (Exception e) { }
	            	} else if (inputArray[0].equalsIgnoreCase("correct predictions")) {
	            		try {
	            			int correctPredictions = Integer.parseInt(inputArray[1]);
	            			this.correctPredictions = correctPredictions;
	            		} catch (Exception e) { }
	            	} else if (inputArray[0].equalsIgnoreCase("total predictions")) {
	            		try {
	            			int totalPredictions = Integer.parseInt(inputArray[1]);
	            			this.totalPredictions = totalPredictions;
	            		} catch (Exception e) { }
	            	} else if (inputArray[0].equalsIgnoreCase("input neurons")) {
	            		// A string array of the input neurons
	    				String[] inputNeurons = inputArray[1].split("],");// Input layer
	    		        inputLayer = new Neuron[inputNeurons.length];
	    				for (int i = 0; i < inputNeurons.length; i++) {
	    					String[] currentNeuron = inputNeurons[i].replace("[", "").replace("]", "").split(",\\{");
	    					float bias = Float.parseFloat(currentNeuron[0]);
	    					inputLayer[i] = new Neuron().setBias(bias);
	    					
	    					String[] weights = currentNeuron[1].replace("}", "").split(",");
	    			        if (inputWeights == null)
	    			        	inputWeights = new float[inputNeurons.length][weights.length];
	    					for (int w = 0; w < weights.length; w++)
	    						inputWeights[i][w] = Float.parseFloat(weights[w]);
	    				}
	            	} else if (inputArray[0].equalsIgnoreCase("hidden neurons")) {
	            		// A string array of the hidden neurons
	    				String[] hiddenNeurons = inputArray[1].split("],");// Hidden layer
	    		        hiddenLayer = new Neuron[hiddenNeurons.length];
	    				for (int i = 0; i < hiddenNeurons.length; i++) {
	    					String[] currentNeuron = hiddenNeurons[i].replace("[", "").replace("]", "").split(",\\{");
	    					float bias = Float.parseFloat(currentNeuron[0]);
	    					hiddenLayer[i] = new Neuron().setBias(bias);
	    					
	    					String[] weights = currentNeuron[1].replace("}", "").split(",");
	    			        if (hiddenWeights == null)
	    			        	hiddenWeights = new float[hiddenNeurons.length][weights.length];
	    					for (int w = 0; w < weights.length; w++)
	    						hiddenWeights[i][w] = Float.parseFloat(weights[w]);
	    				}
	            	} else if (inputArray[0].equalsIgnoreCase("output neurons")) {
	            		// A string array of the output neurons
	    				String[] outputNeurons = inputArray[1].split("],");// Output layer
	    		        outputLayer = new Neuron[outputNeurons.length];
	    				for (int i = 0; i < outputNeurons.length; i++) {
	    					String[] currentNeuron = outputNeurons[i].replace("[", "").replace("]", "").split(",\\{");
	    					float bias = Float.parseFloat(currentNeuron[0]);
	    					outputLayer[i] = new Neuron().setBias(bias);
	    				}
	            	}
	            }
				
				System.out.println("Succesfully stored neural network!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
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
    
    public int getCorrectPredictions() {
    	return correctPredictions;
    }
    
    public int getTotalPredictions() {
    	return totalPredictions;
    }
    
    public File saveNetworkToFile(String filePath, int correctPredictions, int totalPredictions) {
		try {
			PrintWriter writer = new PrintWriter(filePath, "UTF-8");
			writer.println("learning rate: " + learningRate);
			writer.println("correct predictions: " + correctPredictions);
			writer.println("total predictions: " + totalPredictions); 
			
			String inputWeights = "input neurons: ";
	    	for (int i = 0; i < inputNeurons; i++) {
	    		inputWeights += "[";
	    		inputWeights += inputLayer[i].getBias() + ",";
	    		
	    		inputWeights += "{";
	    		for (int iw = 0; iw < this.inputWeights[i].length; iw++) {
	    			inputWeights += this.inputWeights[i][iw];
	    			if (iw < this.inputWeights[i].length - 1)
	    				inputWeights += ",";
	    		}
	    		inputWeights += "}";
	    		inputWeights += "]";
	    		
	    		if (i < inputNeurons-1)
	    			inputWeights += ",";
	    	}
	    	
	    	String hiddenWeights = "hidden neurons: ";
	    	for (int i = 0; i < hiddenNeurons; i++) {
	    		hiddenWeights += "[";
	    		hiddenWeights += hiddenLayer[i].getBias() + ",";
	    		
	    		hiddenWeights += "{";
	    		for (int iw = 0; iw < this.hiddenWeights[i].length; iw++) {
	    			hiddenWeights += this.hiddenWeights[i][iw];
	    			if (iw < this.hiddenWeights[i].length - 1)
	    				hiddenWeights += ",";
	    		}
	    		hiddenWeights += "}";
	    		hiddenWeights += "]";
	    		
	    		if (i < hiddenNeurons-1)
	    			hiddenWeights += ",";
	    	}
	    	
	    	String outputWeights = "output neurons: ";
	    	for (int i = 0; i < outputNeurons; i++) {
	    		outputWeights += "[";
	    		outputWeights += outputLayer[i].getBias();
	    		outputWeights += "]";
	    		
	    		if (i < outputNeurons-1)
	    			outputWeights += ",";
	    	}
	    	
	    	writer.println(inputWeights);
	    	writer.println(hiddenWeights);
	    	writer.println(outputWeights);
	    	writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
    	return new File(filePath);
    }

    public void setLearningRate(float learningRate) {
    	this.learningRate = learningRate;
    }
    
    public float getLearningRate() {
    	return learningRate;
    }
    
}
