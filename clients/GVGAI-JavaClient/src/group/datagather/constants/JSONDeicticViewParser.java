package group.datagather.constants;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JSONDeicticViewParser implements Runnable {

	private QuickSort quickSort;
	private JSONParser parser;
	private File dataFile;

	public JSONDeicticViewParser(File dataFile){
		this.quickSort = new QuickSort();
		this.parser = new JSONParser();
		this.dataFile = dataFile;
	}

//	/**
//	 * Expects input in the form "[4,3,32,0.0 : 10.0,-1.0 : -1.0,122.0]"
//	 */
//	public double[] readObservation(String o, double blockSize, double[] currentAvatarPosition, double[] worldDimension) {
//		o = o.substring(1, o.length() - 1); // throws away the square brackets
//
//		String[] observationInfo = o.split(",");
//		//System.out.println(observationInfo[3]);
//		String[] position = observationInfo[3].split(":");
//		double category = Double.parseDouble(observationInfo[0]);
//		double posX = Double.parseDouble(position[0])/blockSize;
//		double posY = Double.parseDouble(position[1])/blockSize;
//
//		return new double[] {category / CATEGORY_NORMALISATION_FACTOR, posX / worldDimension[0], posY / worldDimension[1], (Math.pow(posX - currentAvatarPosition[0], 2) + Math.pow(posY - currentAvatarPosition[1], 2)) / (Math.pow(worldDimension[0], 2) + Math.pow(worldDimension[1], 2))};
//	}
//
//	public void readStateSpace(ArrayList<String> ss){
//		double[][] distances = new double[ss.size() - 2][2];
//
//		// all character information ( first line )
//		String[] avatarInfo = ss.get(0).substring(2).split(";");
//
//		double blockSize = Double.parseDouble(avatarInfo[5]);
//
//		double gameScore = Double.parseDouble(avatarInfo[0]) / NORMALISATION_FACTOR;
//		double avatarSpeed = Double.parseDouble(avatarInfo[6]) / NORMALISATION_FACTOR;
//		double avatarHealthPoints = Double.parseDouble(avatarInfo[11]) / NORMALISATION_FACTOR;
//		double[] worldDimension = readWorldDimensionAndNormalise(avatarInfo[4], blockSize);
//		double[] currentAvatarOrientation = readWorldDimensionAndNormalise(avatarInfo[7], 1.0);
//		double[] currentAvatarPosition = readCoordinateAndNormalise(avatarInfo[8], blockSize, worldDimension);
//		currentAvatarPosition[0] /= worldDimension[0];
//		currentAvatarPosition[1] /= worldDimension[1];
//
//		// read in all observations
//		final int numberOfDataFromObservation = 4;
//		double[][] readObservations = new double[distances.length][numberOfDataFromObservation];
//		for (int i = 0; i < distances.length; i++) readObservations[i] = this.readObservation(ss.get(i+1), blockSize, currentAvatarPosition, worldDimension);
//
//		for(int i = 0; i < distances.length; i++){
//			distances[i][0] = i;
//			distances[i][1] = readObservations[i][3];
//		}
//
//		// sorting
//		this.quickSort.sort(distances);
//		int size = Math.min(distances.length, DV);
//		double[][] selectedObservations = new double[size][numberOfDataFromObservation];
//		for (int i = 0; i < size; i++) selectedObservations[i] = readObservations[(int) distances[i][0]];
//
//		String actionString = ss.get(ss.size() - 1).substring(2, ss.get(ss.size() - 1).length() - 1);
//		double actionPerformed;
//		if (actionString.equals("ACTION_RIGHT")) actionPerformed = RIGHT;
//		else if (actionString.equals("ACTION_LEFT"))  actionPerformed = LEFT;
//		else if (actionString.equals("ACTION_UP")) actionPerformed = UP;
//		else if (actionString.equals("ACTION_DOWN")) actionPerformed = DOWN;
//		else actionPerformed = USE;
//		actionPerformed /= AVAILABLE_ACTIONS;
//
//		String result = "{\n";
//		result += gameScore + ";" + avatarSpeed + ";" + avatarHealthPoints + ";" + currentAvatarOrientation[0] + ";" + currentAvatarOrientation[1] + ";" + currentAvatarPosition[0] + ";" + currentAvatarPosition[1] + ";" + actionPerformed + "\n";
//		for(int i = 0; i < selectedObservations.length; i++){
//			result += selectedObservations[i][0] + ";" + selectedObservations[i][1] + ";" + selectedObservations[i][2] + ";" + selectedObservations[i][3] + "\n";
//		}
//		result += "}";
//
//		this.allStates.add(result);
//	}
//
//	/**
//	 * Expects input in the form "[12.0, 110.0]"
//	 */
//	public double[] readCoordinateAndNormalise(String coordinates, double blockSize, double[] worldDimension) {
//		String[] position = coordinates.split(",");
//		position[0] = position[0].replace("[", "");
//		position[1] = position[1].replace("]", "");
//		return new double[] {Double.parseDouble(position[0])/blockSize/worldDimension[0], Double.parseDouble(position[1])/blockSize/worldDimension[1]};
//	}
//
//	public double[] readWorldDimensionAndNormalise(String coordinates, double blockSize) {
//		String[] position = coordinates.split(",");
//		position[0] = position[0].replace("[", "");
//		position[1] = position[1].replace("]", "");
//		return new double[] {Double.parseDouble(position[0])/blockSize, Double.parseDouble(position[1])/blockSize};
//	}
//
//	public void readFile(File level) throws IOException{
//		System.out.println(level.getAbsolutePath());
//		FileReader fr = new FileReader(level);
//		BufferedReader bf = new BufferedReader(fr);
//		String s;
//		ArrayList<String> ss;
//		this.allStates = new ArrayList<>();
//		while((s = bf.readLine()) != null){
//			if(s.charAt(0) == '{' && s.charAt(1) == '{'){
//				//New Statespace begins
//				ss = new ArrayList<>();
//				ss.add(s);
//				String o;
//				while((o = bf.readLine()) != null){
//					ss.add(o);
//					if(o.charAt(0) == '}'){
//						//End of observation
//						break;
//					}
//				}
//				this.readStateSpace(ss);
//			}
//		}
//
//		Thread thread = new Thread(new DataSaver(this.allStates, new File("clients/GVGAI-JavaClient/src/" + Constants.PREPROCESSED_OUTPUT_DIR + level.getName())));
//		thread.start();
//		this.allStates = new ArrayList<>();
//	}
//	public void selectFiles(File folder) throws IOException{
//		for (final File fileEntry : folder.listFiles()) {
//			System.out.println(fileEntry.getName());
//			this.readFile(fileEntry);
//		}
//	}
//	public void print(ArrayList<String> als){
//		for(String s : als){
//			System.out.println(s);
//		}
//	}
//	public void print(String[] s){
//		for(int i = 0; i < s.length; i++){
//			System.out.println(s[i]);
//		}
//	}
//

	public static void start() {
		ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		File folder = new File("clients/GVGAI-JavaClient/src/" + Constants.RAW_OUTPUT_DIR);
		String[] filePaths = folder.list();
		for (int i = 0; i < filePaths.length; i++)
			service.submit(new JSONDeicticViewParser(new File(filePaths[i])));

		service.shutdown();
	}








	@Override
	public void run() {
		JSONObject object = null;
		try {
			object = (JSONObject) this.parser.parse(new FileReader(this.dataFile));
		} catch (IOException e) {
			System.err.println("Reading of file " + this.dataFile + " failed");
			return;
		} catch (ParseException e) {
			System.err.println("Reading of file " + this.dataFile + " failed");
			return;
		}
	}

	private JSONObject parseState(JSONObject state) {
		JSONObject result = new JSONObject();

		//		String result = "{\n";
//		result += gameScore + ";" + avatarSpeed + ";" + avatarHealthPoints + ";" + currentAvatarOrientation[0] + ";" + currentAvatarOrientation[1] + ";" + currentAvatarPosition[0] + ";" + currentAvatarPosition[1] + ";" + actionPerformed + "\n";
//		for(int i = 0; i < selectedObservations.length; i++){
//			result += selectedObservations[i][0] + ";" + selectedObservations[i][1] + ";" + selectedObservations[i][2] + ";" + selectedObservations[i][3] + "\n";
//		}
//		result += "}";

		result.put("gameScore", state.get("gameSCore"));
		result.put("avatarSpeed", state.get("avatarSpeed"));
		result.put("avatarHealthPoints", state.get("avatarHealthPoints"));

		return result;
	}

	public static void main(String[] args) throws IOException {
		start();
	}
}
