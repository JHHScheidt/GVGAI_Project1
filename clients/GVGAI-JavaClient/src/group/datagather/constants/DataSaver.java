package group.datagather.constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DataSaver implements Runnable {

	private ArrayList<String> data;
	private File outputFile;

	public DataSaver(ArrayList<String> data, File outputFile) {
		this.data = data;
		this.outputFile = outputFile;
	}

	@Override
	public void run() {
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(this.outputFile));
			for (String string : this.data)
				writer.print(string + "\n");

			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


}
