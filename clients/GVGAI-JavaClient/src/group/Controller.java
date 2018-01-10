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

	public static final int STATE_DATA_AMOUNT = 8; // data without observations
	public static final int OBSERVATIONS = 10;
	public static final int INPUT_DIMENSION = STATE_DATA_AMOUNT + 4 * OBSERVATIONS;
	public static final int ENCODED_SIZE = 3;

	private NeuralNetwork network;
	private Learner learner;

	public void init() {
		this.initNetwork();
		this.initLearner();
	}

	private void initNetwork() {
		long start = System.nanoTime();

		this.network = new NeuralNetwork(
				new Layer(INPUT_DIMENSION + 1, true),
				new Layer(INPUT_DIMENSION + 1 - 5, true),
				new Layer(INPUT_DIMENSION + 1 - 10, true),
				new Layer(INPUT_DIMENSION + 1 - 15, true),
				new Layer(INPUT_DIMENSION + 1 - 20, true),
				new Layer(INPUT_DIMENSION + 1 - 25, true),
				new Layer(INPUT_DIMENSION + 1 - 30, true),
				new Layer(INPUT_DIMENSION + 1 - 35, true),
				new Layer(INPUT_DIMENSION + 1 - 40, true),
				new Layer(ENCODED_SIZE, false));
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
		this.learner.initSpace(this.network);

		System.out.println("initialising learner took " + (System.nanoTime() - start) + " nanoseconds");
	}

	public static void main(String[] args) {
		Controller controller = new Controller();
		controller.init();
	}
}
