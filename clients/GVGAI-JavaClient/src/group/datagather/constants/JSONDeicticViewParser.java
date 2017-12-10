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
		System.out.println("Running for file " + this.dataFile);
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

		File outputFile = new File("GVGAI_Project1/clients/GVGAI-JavaClient/src/" + Constants.PREPROCESSED_OUTPUT_DIR + this.dataFile.getName());
		try {
			outputFile.createNewFile();
			PrintWriter writer = new PrintWriter(new FileOutputStream(outputFile));
			writer.write(result.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JSONObject parseState(JSONObject state) {
		JSONObject result = new JSONObject();

		double blockSize = Double.parseDouble(state.get("blockSize").toString());
		double width = Double.parseDouble(((JSONArray) state.get("worldDimension")).get(0).toString()) / blockSize;
		double height = Double.parseDouble(((JSONArray) state.get("worldDimension")).get(1).toString()) / blockSize;

		result.put("gameScore", Double.parseDouble(state.get("gameScore").toString()) / Constants.NORMALISATION_FACTOR);
		result.put("avatarSpeed", Double.parseDouble(state.get("avatarSpeed").toString()) / Constants.NORMALISATION_FACTOR);
		result.put("avatarHealthPoints", Double.parseDouble(state.get("avatarHealthPoints").toString()) / Constants.NORMALISATION_FACTOR);
		result.put("avatarOrientation", state.get("avatarOrientation"));

		JSONArray position = (JSONArray) state.get("avatarPosition");
		JSONArray normalisedPosition = new JSONArray();
		normalisedPosition.add(Double.parseDouble(position.get(0).toString()) / blockSize / width);
		normalisedPosition.add(Double.parseDouble(position.get(1).toString()) / blockSize / height);
		result.put("avatarPosition", normalisedPosition);

		double x = Double.parseDouble(normalisedPosition.get(0).toString());
		double y = Double.parseDouble(normalisedPosition.get(1).toString());

		JSONArray observations = (JSONArray) state.get("observations");
		double[][] observationDistances = new double[observations.size()][2];
		double observationX, observationY;
		for (int i = 0; i < observationDistances.length; i++) {
			JSONArray observationPosition = (JSONArray) ((JSONObject) observations.get(i)).get("position");
			observationX = Double.parseDouble(observationPosition.get(0).toString()) / blockSize / width;
			observationY = Double.parseDouble(observationPosition.get(1).toString()) / blockSize / height;

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

			JSONArray tempPosition = (JSONArray) temp.get("position");
			tempPosition.set(0, Double.parseDouble(tempPosition.get(0).toString()) / blockSize / width);
			tempPosition.set(1, Double.parseDouble(tempPosition.get(1).toString()) / blockSize / height);
			temp.put("category", Integer.parseInt(temp.get("category").toString()) / Constants.CATEGORY_NORMALISATION_FACTOR);
			temp.remove("itype");
			temp.remove("reference");
			temp.remove("obsId");
			selectedObservations.add(temp);
		}
		result.put("observations", selectedObservations);

		return result;
	}

	public static void main(String[] args) throws IOException {
		ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		File folder = new File("GVGAI_Project1/clients/GVGAI-JavaClient/src/" + Constants.RAW_OUTPUT_DIR);
		File[] files = folder.listFiles();
		System.out.println(folder.getAbsolutePath());
		for (int i = 0; i < files.length; i++)
			service.submit(new JSONDeicticViewParser(files[i]));

		service.shutdown();
	}
}
