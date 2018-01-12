package group.neuralnet;

public class Layer {

	private Neuron[] neuronsWithoutBias;
	private Neuron[] neurons;
	
	private Layer next;

	private boolean bias;
	
	public Layer(int neurons, boolean bias) {
		this.neurons = new Neuron[neurons];
		for (int i = 0; i < this.neurons.length; i++) {
			if (i == 0 && bias) this.neurons[i] = new Neuron(true);
			else this.neurons[i] = new Neuron(false);
		}
		
		if (bias) {
			this.neuronsWithoutBias = new Neuron[neurons - 1];
			for (int i = 0; i < this.neuronsWithoutBias.length; i++) {
				this.neuronsWithoutBias[i] = this.neurons[i + 1];
			}
		} else this.neuronsWithoutBias = this.neurons;
		
		this.bias = bias;
	}
	
	public void feedForward() {
		Neuron[] nextNeurons = this.next.getNeuronsWithoutBias();
		for (int i = 0; i < nextNeurons.length; i++) {

			double sum = 0;
			for (int j = 0; j < this.neurons.length; j++) {
				sum += this.neurons[j].getWeights()[i] * this.neurons[j].getActivation();
			}
			
			nextNeurons[i].setActivation(NeuralNetwork.sigmoid(sum));
		}
	}
	
	public void backPropagate() {
		for (int i = 0; i < this.neurons.length; i++)
			this.neurons[i].backPropogate();
	}
	
	public void updateWeights() {
		for (Neuron neuron : this.neurons) neuron.updateWeights();
	}
	
	/**
	 * Must be called after next layer has been set
	 */
	public void init() {
		if (this.next == null) return; // can't set next ones, no weights, this is output layer
		
		for (Neuron neuron : this.neurons) {
			neuron.setNextNeurons(this.next.getNeuronsWithoutBias());
			neuron.init();
		}
	}
	
	public void setNextLayer(Layer layer) {
		this.next = layer;
	}
	
	public boolean containsBias() {
		return this.bias;
	}
	
	public Neuron[] getNeurons() {
		return this.neurons;
	}
	
	public Neuron[] getNeuronsWithoutBias() {
		return this.neuronsWithoutBias;
	}
	
	public String toString(boolean weights) {
		String result = "[Layer: ";
		for (Neuron neuron : this.neurons) result += neuron.toString(weights);
		return result += "]";
	}
}
