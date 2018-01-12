package group.learning.q;

import group.Controller;
import group.datagather.Input;
import group.datagather.constants.Constants;
import group.learning.q.octree.Octree;
import group.learning.q.octree.Point;
import group.neuralnet.NeuralNetwork;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Learner {

	private Octree space;

	public Learner() {
		this.space = new Octree();
	}

	public void initSpace(NeuralNetwork network) {
		// read data
		JSONArray array = null;
		try {
			array = (JSONArray) new JSONParser().parse(new FileReader(new File(Constants.PREPROCESSED_OUTPUT_DIR + "0_1.txt")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// parse data
		ArrayList<Input> inputs = new ArrayList<>();
		ArrayList<Input> nextInputs = new ArrayList<>();
		ArrayList<Double> rewards = new ArrayList<>();

		Input input;
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = (JSONObject) array.get(i);

			JSONObject state = (JSONObject) object.get("state");
			input = this.parseStateToInput(state);
			input.setAction(Double.parseDouble(object.get("action").toString()));
			inputs.add(input);

			input = this.parseStateToInput((JSONObject) object.get("newState"));
			nextInputs.add(input);

			rewards.add((Double.parseDouble(((JSONObject) object.get("newState")).get("gameScore").toString()) - Double.parseDouble(state.get("gameScore").toString())) * Constants.SCORE_NORMALISATION_FACTOR);
		}

		// use data
		Point[] points = new Point[inputs.size()];
		Point[][] nextPoints = new Point[inputs.size()][(int) Constants.AVAILABLE_ACTIONS];

		double[] networkInput = new double[Controller.INPUT_DIMENSION];
		double[] output;
		int inputId;
		for (int i = 0; i < points.length; i++) {
			// find value for current state
			input = inputs.get(i);

			double[][] observations = input.getObservations();

			inputId = 0;
			networkInput[inputId++] = input.getGameScore();
			networkInput[inputId++] = input.getAvatarHealthPoints();
			networkInput[inputId++] = input.getAvatarSpeed();
			networkInput[inputId++] = input.getAvatarOrientationX();
			networkInput[inputId++] = input.getAvatarOrientationY();
			networkInput[inputId++] = input.getAvatarPositionX();
			networkInput[inputId++] = input.getAvatarPositionY();

			for (double[] observation : observations)
				for (double value : observation) networkInput[inputId++] = value;

			networkInput[inputId] = input.getAction();

			output = network.compute(networkInput);
			points[i] = new Point(output[0], output[1], output[2]);

			// find value for next state
			input = nextInputs.get(i);
			inputId = 0;
			networkInput[inputId++] = input.getGameScore();
			networkInput[inputId++] = input.getAvatarHealthPoints();
			networkInput[inputId++] = input.getAvatarSpeed();
			networkInput[inputId++] = input.getAvatarOrientationX();
			networkInput[inputId++] = input.getAvatarOrientationY();
			networkInput[inputId++] = input.getAvatarPositionX();
			networkInput[inputId++] = input.getAvatarPositionY();

			for (double[] observation : observations)
				for (double value : observation) networkInput[inputId++] = value;

			for (int j = 0; j < Constants.AVAILABLE_ACTIONS; j++) {
				networkInput[inputId] = j / Constants.AVAILABLE_ACTIONS;

				output = network.compute(networkInput);
				nextPoints[i][j] = new Point(output[0], output[1], output[2]);
			}
		}

		// build the space
		System.out.println("data points: " + points.length);
		for (Point point : points) this.space.addPoint(point);

		// train
		double bestQValue = Double.MIN_VALUE;
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < nextPoints[i].length; j++) {
				double value = this.space.findValue(nextPoints[i][j]);
				if (value > bestQValue) bestQValue = value;
			}

			this.space.addData(points[i], Constants.LEARNING_RATE * (rewards.get(i) + Constants.DISCOUNT_FACTOR * bestQValue)); // TODO this value needs to be the correct q value update
		}
	}

	private Input parseStateToInput(JSONObject state) {
		Input input = new Input();

		input.setGameScore(Double.parseDouble(state.get("gameScore").toString()));
		input.setAvatarHealthPoints(Double.parseDouble(state.get("avatarHealthPoints").toString()));
		input.setAvatarSpeed(Double.parseDouble(state.get("avatarSpeed").toString()));
		input.setAvatarOrientationX(Double.parseDouble(((JSONArray) state.get("avatarOrientation")).get(0).toString()));
		input.setAvatarOrientationY(Double.parseDouble(((JSONArray) state.get("avatarOrientation")).get(1).toString()));
		input.setAvatarPositionX(Double.parseDouble(((JSONArray) state.get("avatarPosition")).get(0).toString()));
		input.setAvatarPositionY(Double.parseDouble(((JSONArray) state.get("avatarPosition")).get(1).toString()));

		JSONArray observations = (JSONArray) state.get("observations");
		for (int o = 0; o < observations.size(); o++) {
			JSONObject observation = (JSONObject) observations.get(o);
			JSONArray observationPosition = (JSONArray) observation.get("position");
			input.addObservation(
					Double.parseDouble(observation.get("sqDist").toString()),
					Double.parseDouble(observation.get("category").toString()),
					Double.parseDouble(observationPosition.get(0).toString()),
					Double.parseDouble(observationPosition.get(1).toString()));
		}

		return input;
	}
}
