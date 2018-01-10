package group;

import group.datagather.constants.Constants;
import group.learning.q.Learner;
import group.neuralnet.Layer;
import group.neuralnet.NeuralNetwork;

import java.io.IOException;

/**
 * Class used for managing the offline work
 */
public class Controller {

	private NeuralNetwork network;
	private Learner learner;

	public void init() {
		this.initNetwork();
		this.initLearner();
	}

	private void initNetwork() {
		long start = System.nanoTime();

		final int STATE_DATA_AMOUNT = 8; // data without observations
		final int OBSERVATIONS = 10;
		final int INPUT_DIMENSION = STATE_DATA_AMOUNT + 4 * OBSERVATIONS;
		final int ENCODED_SIZE = 3;
		final int OUTPUT_DIMENSION = STATE_DATA_AMOUNT + 4 * OBSERVATIONS - 1;

		this.network = new NeuralNetwork(
				new Layer(INPUT_DIMENSION + 1, true),
				new Layer(STATE_DATA_AMOUNT + OBSERVATIONS + 1, true),
				new Layer(ENCODED_SIZE + 1, true),
				new Layer(STATE_DATA_AMOUNT + OBSERVATIONS + 1, true),
				new Layer(OUTPUT_DIMENSION, false));
		this.network.init();
		try {
			this.network.loadWeights(Constants.NETWORK_WEIGHTS_DIR + "weights.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("initialising network took " + (System.nanoTime() - start) + " nanoseconds");
	}

	private void initLearner() {
		long start = System.nanoTime();

		this.learner = new Learner();
		this.learner.initSpace();

		System.out.println("initialising learner took " + (System.nanoTime() - start) + " nanoseconds");
	}

	public static void main(String[] args) {
		Controller controller = new Controller();
	}
}
