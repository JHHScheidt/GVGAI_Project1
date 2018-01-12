package tracks.singlePlayer.custom;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class JSONSaver implements Runnable {

	private ArrayList<JSONObject> data;

	public JSONSaver(ArrayList<JSONObject> data) {
		this.data = data;
	}

	@Override
	public void run() {
		File path = new File(Constants.OUTPUT_DIR);
		if (!path.exists()) path.mkdirs();

		File outputFile = new File(Constants.OUTPUT_DIR + Constants.CURRENT_GAME_ID +"_" + Constants.CURRENT_LEVEL_ID + "(A"+Constants.AGENT_ID+"-P"+ Constants.CURRENT_GAME_ITER + ")"+ ".txt");
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(outputFile));
			JSONArray finalObject = new JSONArray();
			for (JSONObject object : this.data)
				finalObject.add(object);

			writer.print(finalObject.toJSONString());

			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
