package group.datagather;

import group.datagather.constants.Constants;
import group.datagather.constants.QuickSort;
import group.neuralnet.Layer;
import group.neuralnet.NeuralNetwork;
import serialization.Observation;
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

	private static final int I_BITS = 13, J_BITS = 13, K_BITS = 3;

	private NeuralNetwork network;
	private QuickSort quickSort;
	
	@Override
	public void init(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
		this.network = new NeuralNetwork(	new Layer(INPUT_DIMENSION + 1, true),
											new Layer(STATE_DATA_AMOUNT + OBSERVATIONS + 1, true),
											new Layer(ENCODED_SIZE + 0, false));
		try {
			this.network.loadWeights(Constants.NETWORK_WEIGHTS_DIR + "weights.txt");
		} catch (IOException e) {
			System.err.println("Loading the weights went wrong! quitting");
			System.exit(0);
		}

		this.quickSort = new QuickSort();
	}

	@Override
	public Types.ACTIONS act(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
		double[] networkInput = new double[INPUT_DIMENSION];
		networkInput[0] = sso.getGameScore() / Constants.SCORE_NORMALISATION_FACTOR;
		networkInput[1] = sso.getAvatarSpeed() / Constants.SCORE_NORMALISATION_FACTOR;
		networkInput[2] = sso.getAvatarHealthPoints() / Constants.SCORE_NORMALISATION_FACTOR;
		networkInput[3] = sso.getAvatarOrientation()[0];
		networkInput[4] = sso.getAvatarOrientation()[1];
		networkInput[5] = sso.getAvatarPosition()[0] / sso.blockSize / sso.worldDimension[0];
		networkInput[6] = sso.getAvatarPosition()[1] / sso.blockSize / sso.worldDimension[1];

		int size = 0;
		for (int i = 0; i < sso.getObservationGrid().length; i++)
			for (int j = 0; j < sso.getObservationGrid()[i].length; j++)
				size += sso.getObservationGrid()[i][j].length;

		double[][] distances = new double[size][2];
		int currentId = 0;


		System.out.println("reached here 2");
		Observation[][][] observations = sso.getObservationGrid();
		int index;
		for (int i = 0; i < observations.length; i++) {
			for (int j = 0; j < observations[i].length; j++) {
				for (int k = 0; k < observations[i][j].length; k++) {
					index = this.encodeIndex(i, j, k);
					distances[currentId][0] = index;
					distances[currentId++][1] = observations[i][j][k].sqDist;
				}
			}
		}

		this.quickSort.sort(distances);
		int i, j, k;
		Observation observation;
		for (int z = 0; z < Constants.DV; z++) {
			index = (int) distances[z][0];
			i = (index >> (J_BITS + K_BITS)) & (1 << I_BITS) - 1;
			j = (index >> K_BITS) & (1 << J_BITS) - 1;
			k = index & (1 << K_BITS) - 1;

			observation = observations[i][j][k];
			networkInput[7 + z * 4] = observation.category / Constants.SCORE_NORMALISATION_FACTOR;
			networkInput[7 + z * 4 + 1] = observation.position.x / sso.blockSize / sso.worldDimension[0];
			networkInput[7 + z * 4 + 2] = observation.position.y / sso.blockSize / sso.worldDimension[1];
			networkInput[7 + z * 4 + 3] = observation.sqDist;
		}

		// double bestQValue = -1000
		// Types.ACTIONS bestAction = Types.ACTIONS.NIL;
		for (Types.ACTIONS action : sso.getAvailableActions()) {
			networkInput[networkInput.length - 1] = action.ordinal() / Constants.AVAILABLE_ACTIONS;

			double[] output = this.network.compute(networkInput);
			// double qValue = QLearner.qLearn(output);
			// if (qValue > bestQValue) {
			// bestQValue = qValue;
			// bestAction = action;
			//}
		}


		System.out.println(elapsedTimer.elapsed());

		// return bestAction;
		return null;
	}

	@Override
	public int result(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
		return 0;
	}

	private int encodeIndex(int i, int j, int k) {
		int index = (i << J_BITS);
		index += j;
		index = (index << K_BITS);
		return index + k;
	}
}
