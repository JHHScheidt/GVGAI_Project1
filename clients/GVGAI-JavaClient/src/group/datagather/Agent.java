package group.datagather;

import group.datagather.constants.Constants;
import group.datagather.neuralnet.Layer;
import group.datagather.neuralnet.NeuralNetwork;
import serialization.SerializableStateObservation;
import serialization.Types;
import utils.AbstractPlayer;
import utils.ElapsedCpuTimer;

import java.io.IOException;

public class Agent extends AbstractPlayer {

	private static final int STATE_DATA_AMOUNT = 8; // data without observations
	private static final int OBSERVATIONS = 10;
	private static final int INPUT_DIMENSION = STATE_DATA_AMOUNT + 4 * OBSERVATIONS;
	private static final int ENCODED_SIZE = 3;
	private static final int OUTPUT_DIMENSION = STATE_DATA_AMOUNT + 4 * OBSERVATIONS - 1;

	private NeuralNetwork network;
	
	@Override
	public void init(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
		this.network = new NeuralNetwork(	new Layer(INPUT_DIMENSION + 1, true),
											new Layer(STATE_DATA_AMOUNT + OBSERVATIONS + 1, true),
											new Layer(ENCODED_SIZE + 1, true),
											new Layer(STATE_DATA_AMOUNT + OBSERVATIONS + 1, true),
											new Layer(OUTPUT_DIMENSION + 0, false));
		try {
			this.network.loadWeights(Constants.NETWORK_WEIGHTS_DIR + "name.txt");
		} catch (IOException e) {
			System.err.println("Loading the weights went wrong! quitting");
			System.exit(0);
		}
	}

	@Override
	public Types.ACTIONS act(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
		long start = System.nanoTime();

		double[] networkInput = new double[INPUT_DIMENSION];
		networkInput[0] = sso.getGameScore() / Constants.NORMALISATION_FACTOR;
		networkInput[1] = sso.getAvatarSpeed() / Constants.NORMALISATION_FACTOR;
		networkInput[2] = sso.getAvatarHealthPoints() / Constants.NORMALISATION_FACTOR;
		networkInput[3] = sso.getAvatarOrientation()[0];
		networkInput[4] = sso.getAvatarOrientation()[1];
		networkInput[5] = sso.getAvatarPosition()[0] / sso.blockSize / sso.worldDimension[0];
		networkInput[6] = sso.getAvatarPosition()[1] / sso.blockSize / sso.worldDimension[1];

//
//				observations
//
//				door dat ding heen
//
//
//		networkInput[networkInput.length - 1] = ... acties


		System.out.println((System.nanoTime() - start) / 1000000.0);

		return null;
	}

	@Override
	public int result(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
		return 0;
	}
}
