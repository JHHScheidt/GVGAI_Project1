package group.datagather.constants;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

public class Utils {

	public static ArrayList<Object> readQSpace(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String qSpaceString = reader.readLine();

		ArrayList<Object> result = null;
		Stack<ArrayList<Object>> listStack = new Stack<>();

		int start = 0;
		boolean initial = false;
		for (int i = 0; i < qSpaceString.length(); i++) {
			if (qSpaceString.charAt(i) == '[') {
				initial = true;
				if (listStack.isEmpty()) {
					result = new ArrayList<>();
					listStack.push(result);
				} else listStack.push(new ArrayList<>());
			} else {
				if (initial) {
					start = i;
					initial = false;
				}
				if (qSpaceString.charAt(i) == ']') {

					if (qSpaceString.charAt(i - 1) != ']') {
						String values = qSpaceString.substring(start, i);
						start = i + 1;

						String[] split = values.split(",");
						for (String value : split) {
							listStack.peek().add(Double.parseDouble(value));
						}
					}

					ArrayList<Object> completedList = listStack.pop();
					if (listStack.isEmpty()) listStack.peek().add(completedList);
				}
			}
		}

		return result;
	}

	public static void main(String[] args) {
		try {
			readQSpace(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
