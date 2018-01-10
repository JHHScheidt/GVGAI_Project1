package group.neuralnet;

import java.text.DecimalFormat;;

public class Neuron {

	private static final DecimalFormat FORMATTER = new DecimalFormat("#.##");

	private double[] weights;
	private Neuron[] next;

	private double activation;
	private double error;

	public Neuron(boolean bias) {
		if (bias)
			this.activation = 1.0;
	}

	/**
	 * Must be called after next neurons have been set
	 */
	public void init() {
		this.weights = new double[this.next.length];
		for (int i = 0; i < this.weights.length; i++) {
			this.weights[i] = Math.random() * 0.01;
		}
	}

	public void backPropogate() {
		double value = 0;

		for (int i = 0; i < this.next.length; i++) {
			value += this.weights[i] * this.next[i].getError();
		}

		this.error = value * NeuralNetwork.sigmoidDerivative(this.activation);
	}

	public void updateWeights() {
		for (int i = 0; i < this.next.length; i++) {
			this.weights[i] += NeuralNetwork.LEARNING_RATE * this.activation * this.next[i].getError();
		}
	}

	public void setNextNeurons(Neuron[] next) {
		this.next = next;
	}

	public double getActivation() {
		return this.activation;
	}

	public void setActivation(double activation) {
		this.activation = activation;
	}

	public double[] getWeights() {
		return this.weights;
	}

	public void setWeights(double[] weights) {
		this.weights = weights;
	}

	public double getError() {
		return this.error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public String toString(boolean weights) {
		String result = "[Neuron: activation: " + FORMATTER.format(this.activation);

		if (weights) {
			if (this.weights != null) {
				result += "\nweights: ";

				for (double weight : this.weights) {
					result += "{" + weight + "}\n";
				}
			}
		}

		return result + "]";
	}
}
