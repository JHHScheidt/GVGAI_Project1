package group.datagather.neuralnet;

import group.datagather.constants.Constants;

import java.io.IOException;

public class Base {

	public static void main(String[] args) {

		long start = System.nanoTime();

		final int STATE_DATA_AMOUNT = 8; // data without observations
		final int OBSERVATIONS = 10;
		final int INPUT_DIMENSION = STATE_DATA_AMOUNT + 4 * OBSERVATIONS;
		final int ENCODED_SIZE = 3;
		final int OUTPUT_DIMENSION = STATE_DATA_AMOUNT + 4 * OBSERVATIONS - 1;

		NeuralNetwork network = new NeuralNetwork(	new Layer(INPUT_DIMENSION + 1, true),
													new Layer(STATE_DATA_AMOUNT + OBSERVATIONS + 1, true),
													new Layer(ENCODED_SIZE + 1, true),
													new Layer(STATE_DATA_AMOUNT + OBSERVATIONS + 1, true),
													new Layer(OUTPUT_DIMENSION, false));
		//network.init();
		//network.saveWeights();
		try {
			network.loadWeights("clients/GVGAI-JavaClient/src/" + Constants.NETWORK_WEIGHTS_DIR + "name.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Object creation took: " + (System.nanoTime() - start));



//
//		double x = 1;
//
//		boolean converged = false;
//		double[][] input = new double[][] { new double[] {x, 0, 0, 0, 0, 0, 0, 0},
//											new double[] {0, x, 0, 0, 0, 0, 0, 0},
//											new double[] {0, 0, x, 0, 0, 0, 0, 0},
//											new double[] {0, 0, 0, x, 0, 0, 0, 0},
//											new double[] {0, 0, 0, 0, x, 0, 0, 0},
//											new double[] {0, 0, 0, 0, 0, x, 0, 0},
//											new double[] {0, 0, 0, 0, 0, 0, x, 0},
//											new double[] {0, 0, 0, 0, 0, 0, 0, x}};
//
//		for (int i = 0; i < 100000; i++) {
//			double sum = 0;
//			for (int d = 0; d < input.length; d++) {
//				network.forwardFeed(input[d]);
//				network.backPropogate(input[d]);
//				network.updateWeights();
//
//				sum += network.getAverageOutputError();
//			}
//
//			if (!converged && Math.abs((sum / input.length)) < 0.01) {
//				System.out.println("converged after " + i + " iterations");
//				converged = true;
//			}
//		}
//
//		System.out.println(network.toString(true));
//		for (int i = 0; i < input.length; i++) {
//			System.out.println("input " + i);
//			double[] result = network.compute(input[i]);
//			System.out.println(network.toString(false));
//			printArray(result);
//		}
	}
	
	public static void printArray(double[] array) {
		for (double d : array) {
			System.out.print(d + " ");
		}
		System.out.println();
	}
	

}
