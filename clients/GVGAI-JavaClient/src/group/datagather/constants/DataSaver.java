package group.datagather.constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DataSaver implements Runnable {

	private ArrayList<Tuple> data;

	public DataSaver(ArrayList<Tuple> data) {
		this.data = data;
	}

	@Override
	public void run() {
		File outputFile = new File(Constants.OUTPUT_DIR + Constants.CURRENT_GAME_ID + "_" + Constants.CURRENT_LEVEL_ID + ".txt");
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(outputFile));
			for (Tuple tuple : this.data)
				writer.print(tuple + "\n");

			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
