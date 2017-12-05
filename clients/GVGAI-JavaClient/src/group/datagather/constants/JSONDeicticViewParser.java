package group.datagather.constants;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static group.datagather.constants.Constants.*;

public class JSONDeicticViewParser implements Runnable {

	private QuickSort quickSort;
	private JSONParser parser;
	private File dataFile;

	public JSONDeicticViewParser(File dataFile){
		this.quickSort = new QuickSort();
		this.parser = new JSONParser();
		this.dataFile = dataFile;
	}

	@Override
	public void run() {
		JSONArray array = null;
		try {
			array = (JSONArray) this.parser.parse(new FileReader(this.dataFile));
		} catch (IOException e) {
			System.err.println("Reading of file " + this.dataFile + " failed");
			return;
		} catch (ParseException e) {
			System.err.println("Parsing of file " + this.dataFile + " failed");
			return;
		}

		JSONArray result = new JSONArray();
		for (int i = 0; i < array.size(); i++) {
			JSONObject sans = (JSONObject) array.get(i);

			if (!sans.containsKey("newState")) continue;

			JSONObject subResult = new JSONObject();
			String actionString = (String) sans.get("action");

			double actionPerformed;
			if (actionString.equals("ACTION_RIGHT")) actionPerformed = RIGHT;
			else if (actionString.equals("ACTION_LEFT"))  actionPerformed = LEFT;
			else if (actionString.equals("ACTION_UP")) actionPerformed = UP;
			else if (actionString.equals("ACTION_DOWN")) actionPerformed = DOWN;
			else actionPerformed = USE;
			actionPerformed /= AVAILABLE_ACTIONS;


			subResult.put("state", this.parseState((JSONObject) sans.get("state")));
			subResult.put("action", actionPerformed);
			subResult.put("newState", this.parseState((JSONObject) sans.get("newState")));

			result.add(subResult);
		}

		File outputFile = new File(Constants.PREPROCESSED_OUTPUT_DIR + this.dataFile.getName());
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(outputFile));
			writer.print(result.toJSONString());
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private JSONObject parseState(JSONObject state) {
		JSONObject result = new JSONObject();

		double blockSize = (double) state.get("blockSize");
		double width = ((double) ((JSONArray) state.get("worldDimension")).get(0)) / blockSize;
		double height = ((double) ((JSONArray) state.get("worldDimension")).get(1)) / blockSize;

		result.put("gameScore", ((double) state.get("gameScore")) / Constants.NORMALISATION_FACTOR);
		result.put("avatarSpeed", ((double) state.get("avatarSpeed")) / Constants.NORMALISATION_FACTOR);
		result.put("avatarHealthPoints", ((double) state.get("avatarHealthPoints")) / Constants.NORMALISATION_FACTOR);
		result.put("avatarOrientation", state.get("avatarOrientation"));

		JSONArray position = (JSONArray) state.get("avatarPosition");
		JSONArray normalisedPosition = new JSONArray();
		normalisedPosition.add(((double) position.get(0)) / blockSize / width);
		normalisedPosition.add(((double) position.get(1)) / blockSize / height);
		result.put("avatarPosition", normalisedPosition);

		double x = (double) normalisedPosition.get(0);
		double y = (double) normalisedPosition.get(1);

		JSONArray observations = (JSONArray) state.get("observations");
		double[][] observationDistances = new double[observations.size()][2];
		double observationX, observationY;
		for (int i = 0; i < observationDistances.length; i++) {
			JSONArray observationPosition = (JSONArray) ((JSONObject) observations.get(i)).get("position");
			observationX = (double) observationPosition.get(0) / blockSize / width;
			observationY = (double) observationPosition.get(1) / blockSize / height;

			observationDistances[i][0] = i;
			observationDistances[i][1] = Math.pow(observationX - x, 2) + Math.pow(observationY - y, 2);
		}

		// sorting
		this.quickSort.sort(observationDistances);
		int size = Math.min(observationDistances.length, Constants.DV);

		JSONArray selectedObservations = new JSONArray();
		for (int i = 0; i < size; i++) {
			JSONObject temp = (JSONObject) observations.get((int) observationDistances[i][0]);
			temp.put("sqDist", observationDistances[i][1]);
			selectedObservations.add(temp);
		}
		result.put("observations", selectedObservations);

		return result;
	}

	public static void main(String[] args) throws IOException {
		ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		File folder = new File("GVGAI_Project1/clients/GVGAI-JavaClient/src/" + Constants.RAW_OUTPUT_DIR);
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++)
			service.submit(new JSONDeicticViewParser(files[i]));

		service.shutdown();
	}
}
