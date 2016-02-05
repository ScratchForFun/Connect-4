package net.vaagen.fourinarow.neuralnetwork;

/**
 * Created by Magnus on 10/15/2015.
 */
public class Neuron {

    private float bias; // We are not using bias-weights
    private float input;
    private float output;

    //private float targetOutput;

    public Neuron setInput(float input) {
        this.input = input;
        return this;
    }

    public Neuron setBias(float bias) {
        this.bias = bias;
        return this;
    }

    /*public Neuron setTargetOutput(float targetOutput) {
        this.targetOutput = targetOutput;
        return this;
    }

    public float getTargetOutput() {
        return targetOutput;
    }*/

    public void calculateOutput() {
        // Using the logistic function
        output = (float) (1 / (1 + Math.exp(-input)));
    }

    public Neuron setOutput(float output) {
        this.output = output;
        return this;
    }

    public float getOutput() {
        return output;
    }

    public float getBias() {
        return bias;
    }
}
