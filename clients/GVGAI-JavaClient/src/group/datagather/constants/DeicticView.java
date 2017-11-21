package group.datagather.constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Marciano
 */
public class DeicticView {

	private Quicksort quicksort;
	private String dataPath;
	private final int UP = 0;
	private final int DOWN = 1;
	private final int LEFT = 2;
	private final int RIGHT = 3;
	private final int ACTION = 4;
	private final int DV = 10;
	private ArrayList<ArrayList<String>> allStates = new ArrayList<>();
	private double[] currentAvatarPosition;

	public DeicticView(String path){
		this.dataPath = path;
		this.quicksort = new Quicksort();
	}

	public static void main(String[] args) throws IOException{
		//This should be the location of your data files.
		String path = "D:\\Documents\\NetbeansProjects\\GitHub\\Random\\data\\GVGAI";
		DeicticView dv = new DeicticView(path);
		File folder = new File(path);
		dv.selectFiles(folder);
	}


	/**
	 * Expects input in the form "[4,3,32,0.0 : 10.0,-1.0 : -1.0,122.0]"
	 */
	public double[] readObservation(String o, blockSize) {
		o = o.substring(1, o.length() - 1); // throws away the square brackets

		String[] observationInfo = o.split(",");
		//System.out.println(observationInfo[3]);
		String[] position = observationInfo[3].split(":");
		double category = Double.parseDouble(observationInfo[0]);
		double posX = Double.parseDouble(position[0])/blockSize;
		double posY = Double.parseDouble(position[1])/blockSize;

		return new double[] {category, posX, posY, Math.sqrt(Math.pow(posX - this.currentAvatarPosition[0], 2) + Math.pow(posY - this.currentAvatarPosition[1], 2))};
	}

	public void readStateSpace(ArrayList<String> ss){
		this.allStates.add(ss);
		double[][] distances = new double[ss.size()-2][2];

		// all character information ( first line )
		String[] avatarInfo = ss.get(0).substring(2).split(";");

		double blockSize = Double.parseDouble(avatarInfo[5]);

		double gameScore = avatarInfo[0];
		double avatarSpeed = avatarInfo[6];
		double avatarHealthPoints = avatarInfo[11];
		double[] worldDimension = readCoordinateAndNormalise(avatarInfo[4], blockSize);
		double[] currentAvatarOrientation = readCoordinateAndNormalise(avatarInfo[8], 1.0);
		double[] currentAvatarPosition = readCoordinateAndNormalise(avatarInfo[8], blockSize);

		// read in all observations
		final int numberOfDataFromObservation = 4;
		double[][] readObservations = new double[distances.length][numberOfDataFromObservation];
		for (int i = 0; i < distances.length; i++) readObservations[i] = this.readObservation(ss.get(i+1));

		for(int i = 0; i < distances.length; i++){
			distances[i][0] = i;
			distances[i][1] = readObservations[i][3];
		}


		// sorting
		this.quicksort.sort(distances);
		int size = (int) Math.min(distances.length, this.DV);
		double[][] selectedObservations = new double[size][numberOfDataFromObservation];
		for (int i = 0; i < size; i++) {
			selectedObservations[i] = readObservations[distances[i][0]];
		}
	}

	/**
	 * Expects input in the form "[12.0, 110.0]"
	 */
	public double[] readCoordinateAndNormalise(String coordinates, double blockSize) {
		String[] position = coordinates.split(",");
		position[0] = position[0].replace("[", "");
		position[1] = position[1].replace("]", "");
		return new double[] {Double.parseDouble(position[0])/blockSize, Double.parseDouble(position[1])/blockSize};
	}

	public void readFile(File level) throws IOException{
		FileReader fr = new FileReader(level);
		BufferedReader bf = new BufferedReader(fr);
		String s;
		ArrayList<String> ss;
		while((s = bf.readLine()) != null){
			if(s.charAt(0) == '{' && s.charAt(1) == '{'){
				//New Statespace begins
				ss = new ArrayList<>();
				ss.add(s);
				String o;
				while((o = bf.readLine()) != null){
					ss.add(o);
					if(o.charAt(0) == '}'){
						//End of observation
						break;
					}
				}
				this.readStateSpace(ss);
			}
		}
	}
	public void selectFiles(File folder) throws IOException{
		for (final File fileEntry : folder.listFiles()) {
			System.out.println(fileEntry.getName());
			if(fileEntry.getName().charAt(0) == 'd' && fileEntry.getName().charAt(1) == 'v'){
				continue;
			}
			this.readFile(fileEntry);
		}
	}
	public void print(ArrayList<String> als){
		for(String s : als){
			System.out.println(s);
		}
	}
	public void print(String[] s){
		for(int i = 0; i < s.length; i++){
			System.out.println(s[i]);
		}
	}
}
