package group.neuralnet;

import group.datagather.constants.Constants;
import group.datagather.constants.DataSaver;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class NeuralNetwork {

	public static final double LEARNING_RATE = -0.05;
	
	private Layer[] layers;
	
	public NeuralNetwork(Layer... layers) {
		this.layers = layers;
	}
	
	public void init() {
		for (int i = 0; i < this.layers.length; i++) {
			if (i != this.layers.length - 1) this.layers[i].setNextLayer(this.layers[i + 1]);
		}
		
		for (int i = this.layers.length - 1; i > -1; i--) this.layers[i].init();
	}
	
	public double[] compute(double[] inputActivation) {
		this.forwardFeed(inputActivation);
		double[] result = new double[this.layers[this.layers.length - 1].getNeurons().length];
		for (int i = 0; i < this.layers[this.layers.length - 1].getNeurons().length; i++)
			result[i] = this.layers[this.layers.length - 1].getNeurons()[i].getActivation();
		return result;
	}
	
	public void forwardFeed(double[] inputActivation) {
		// set the input activation of the first layer
		Neuron[] neurons = this.layers[0].getNeuronsWithoutBias();
		for (int i = 0; i < neurons.length; i++) neurons[i].setActivation(inputActivation[i]);
		
		for (int i = 0; i < this.layers.length - 1; i++)
			this.layers[i].feedForward();
	}

	public void saveWeights() {
		ArrayList<String> strings = new ArrayList<>();
		for (int i = 0; i < this.layers.length - 1; i++) {
			Layer layer = this.layers[i];
			for (Neuron neuron : layer.getNeurons()) {
				strings.add(Arrays.toString(neuron.getWeights()));
			}
			strings.add("");
		}

		File file = new File(Constants.NETWORK_WEIGHTS_DIR);
		if (!file.exists()) file.mkdirs();
		Thread thread = new Thread(new DataSaver(strings, new File(Constants.NETWORK_WEIGHTS_DIR + "savedWeights.txt")));
		thread.start();
	}

	public void loadWeights(String path) throws IOException {
		File file = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(file));

		int layerId = 0;
		int neuronId = 0;
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.equals("")) {
				layerId++;
				neuronId = 0;
			} else {
				line = line.substring(1, line.length() - 1);
				String[] split = line.split(", ");
				Neuron neuron = this.layers[layerId].getNeurons()[neuronId];
				double[] weights = new double[split.length];
				for (int i = 0; i < split.length; i++) weights[i] = Double.parseDouble(split[i]);
				neuron.setWeights(weights);
				neuronId++;
			}
		}
	}
	
	public void backPropogate(double[] expectedOutput) {
		Neuron[] neurons = this.layers[this.layers.length - 1].getNeuronsWithoutBias();
		for (int i = 0; i < neurons.length; i++) neurons[i].setError(neurons[i].getActivation() - expectedOutput[i]);
		
		for (int i = this.layers.length - 2; i > 0; i--) {
			this.layers[i].backPropagate();
		}
	}
	
	public void updateWeights() {
		for (int i = 0; i < this.layers.length - 1; i++) this.layers[i].updateWeights();
	}
	
	public double getAverageOutputError() {
		double sum = 0;
		for (Neuron neuron : this.layers[this.layers.length - 1].getNeurons()) sum += Math.abs(neuron.getError());
		return sum /= this.layers[this.layers.length - 1].getNeurons().length;
	}
	
	public String toString(boolean weights) {
		String result = "";
		for (int i = 0; i < this.layers.length; i++) {
			result += i + this.layers[i].toString(weights) + "\n";
		}
		return result;
	}

	public static double sigmoid(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}

	public static double relu(double x) {
		return Math.max(x, 0);
	}

	public static double sigmoidDerivative(double x) {
		double sigmoid = sigmoid(x);
		return sigmoid * (1 - sigmoid);
	}
}
