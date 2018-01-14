package group.learning.q.octree;

import group.datagather.constants.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Octree {

	private static final int MAX_DEPTH = 18;

	private boolean initial;
	private Node root;
	private double[] rootBounds;

	public Octree() {
		this.rootBounds = new double[6];
		for (int i = 0; i < this.rootBounds.length; i++)
			this.rootBounds[i] = i % 2;
		this.initial = true;
	}

	/**
	 * Use after the tree has been built ( via add point )
	 * @param point
	 * @param value
	 */
	public void addData(Point point, double value) {
		Node parent = this.root;
		double[] bounds = Arrays.copyOf(this.rootBounds, this.rootBounds.length);

		int subCubeId = this.findSubCubeId(bounds, point);
		while (parent.getChild(subCubeId) != null && !parent.getChild(subCubeId).isLeafNode()) {
			parent = parent.getChild(subCubeId);
			subCubeId = this.findSubCubeId(bounds, point);
			this.updateBounds(bounds, subCubeId);
		}

		Node child = parent.getChild(subCubeId);
		if (child == null) {
			// find value for new node
			double average = 0;
			int counter = 0;
			for (int i = 0; i < 8; i++) {
				Node node = parent.getChild(i);
				if (node != null) {
					counter++;
					average += node.getValue();
				}
			}
			average /= counter;

			Node newNode = new Node(point);
			newNode.addValue(average);
			parent.setChild(subCubeId, newNode);

			// q value update
			newNode.addValue(value);
		} else {
			// q value update
			child.addValue(value);
		}
	}

	public double findValue(double[] pointArray) {
		return this.findValue(new Point(pointArray[0], pointArray[1], pointArray[2]));
	}

	public double findValue(Point point) {
		Node parent = this.root;
		double[] bounds = Arrays.copyOf(this.rootBounds, this.rootBounds.length);

		int subCubeId = this.findSubCubeId(bounds, point);
		while (parent.getChild(subCubeId) != null && !parent.getChild(subCubeId).isLeafNode()) {
			parent = parent.getChild(subCubeId);
			subCubeId = this.findSubCubeId(bounds, point);
			this.updateBounds(bounds, subCubeId);
		}

		Node child = parent.getChild(subCubeId);
		if (child == null) {
			// find value for new node
			double average = 0;
			int counter = 0;
			for (int i = 0; i < 8; i++) {
				Node node = parent.getChild(i);
				if (node != null) {
					counter++;
					average += node.getValue();
				}
			}
			average /= counter;

			Node newNode = new Node(point);
			newNode.addValue(average);
			parent.setChild(subCubeId, newNode);

			// q value update
			return newNode.getValue();
		} else {
			// q value update
			return child.getValue();
		}
	}

	/**
	 * Use to build the tree ( do the splitting ), no data will be stored
	 * @param point
	 */
	public void addPoint(Point point) {
		if (this.initial) {
			this.initial = false;
			this.root = new Node(point);
			return;
		}

		Node parent = this.root;
		double[] bounds = Arrays.copyOf(this.rootBounds, this.rootBounds.length);
		int depth = 0;

		int subCubeId = this.findSubCubeId(bounds, point);
		while (parent.getChild(subCubeId) != null && !parent.getChild(subCubeId).isLeafNode()) {
			parent = parent.getChild(subCubeId);
			subCubeId = this.findSubCubeId(bounds, point);
			this.updateBounds(bounds, subCubeId);
			depth++;
		}

		Node child = parent.getChild(subCubeId);
		if (child == null) {
			Node newNode = new Node(point);
			parent.setChild(subCubeId, newNode);

			Point parentPoint = parent.getAndRemovePoint();
			if (parentPoint != null) this.addPoint(parentPoint);
		} else if (!child.getPoint().equals(point)) {
			if (depth == MAX_DEPTH) return;

			Node newNode = new Node(point);
			child.setChild(subCubeId, newNode);

			Point childPoint = child.getAndRemovePoint();
			if (childPoint != null) this.addPoint(childPoint);
		}
	}

	public int findSubCubeId(double[] bounds, Point point) {
		double iBoundCenter = (bounds[0] + bounds[1]) / 2;
		double jBoundCenter = (bounds[2] + bounds[3]) / 2;
		double kBoundCenter = (bounds[4] + bounds[5]) / 2;

		if (point.getI() < iBoundCenter) {
			if (point.getJ() < jBoundCenter) {
				if (point.getK() < kBoundCenter) return 0;
				else return 4;
			} else {
				if (point.getK() < kBoundCenter) return 2;
				else return 6;
			}
		} else {
			if (point.getJ() < jBoundCenter) {
				if (point.getK() < kBoundCenter) return 1;
				else return 5;
			} else {
				if (point.getK() < kBoundCenter) return 3;
				else return 7;
			}
		}
	}

	private void updateBounds(double[] bounds, int cubeId) {
		double boundsUpdate = (bounds[1] - bounds[0]) / 2;

		if (cubeId == 0 || cubeId == 2 || cubeId == 4 || cubeId == 6)
			bounds[1] -= boundsUpdate;
		else bounds[0] += boundsUpdate;

		if (cubeId == 0 || cubeId == 1 || cubeId == 4 || cubeId == 5)
			bounds[3] -= boundsUpdate;
		else bounds[2] += boundsUpdate;

		if (cubeId == 0 || cubeId == 1 || cubeId == 2 || cubeId == 3)
			bounds[5] -= boundsUpdate;
		else bounds[4] += boundsUpdate;
	}

	public void print() {
		this.print(this.root, -1);
	}

	private void print(Node node, int depth) {
		if (node == null) return;
		if (node.isLeafNode()) System.out.println(depth + " - " + node.getPoint() + " value: " + node.getValue());

		for (int i = 0; i < 8; i++) {
			this.print(node.getChild(i), depth + 1);
		}
	}

	public void save() {
		File folder = new File(Constants.QPSACE_OUTPUT_DIR);
		if (!folder.exists()) folder.mkdirs();
		File outputFile = new File(Constants.QPSACE_OUTPUT_DIR + "space.json");

		JSONObject tree = new JSONObject();
		tree.put("root", this.saveNode(this.root));

		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(outputFile));
			writer.println(tree.toJSONString());
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void load() {
		File file = new File(Constants.QPSACE_OUTPUT_DIR + "space.json");

		JSONObject object = null;
		try {
			object = (JSONObject) new JSONParser().parse(new FileReader(file));
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject root = (JSONObject) object.get("root");
		this.root = this.readNode(root);
	}

	private Node readNode(JSONObject nodeJSON) {
		Node node = new Node();

		boolean isLeafNode = Boolean.parseBoolean(nodeJSON.get("isLeafNode").toString());
		if (isLeafNode) {
			node.setPoint(this.pointFromJSON((JSONObject) nodeJSON.get("point")));
			node.setValue(Double.parseDouble(nodeJSON.get("value").toString()));
		} else {
			for (int i = 0; i < 8; i++) {
				Object object =  nodeJSON.get(String.valueOf(i));
				if (object != null) node.setChild(i, this.readNode((JSONObject) object));
			}
		}

		return node;
	}

	private JSONObject saveNode(Node node) {
		JSONObject nodeJSON = new JSONObject();

		nodeJSON.put("isLeafNode", node.isLeafNode());
		if (node.isLeafNode()) {
			nodeJSON.put("point", this.pointToJSON(node.getPoint()));
			nodeJSON.put("value", node.getValue());
		}

		for (int i = 0; i < 8; i++) {
			Node child = node.getChild(i);
			if (child == null) nodeJSON.put(String.valueOf(i), null);
			else nodeJSON.put(String.valueOf(i), this.saveNode(child));
		}

		return nodeJSON;
	}

	private JSONObject pointToJSON(Point point) {
		JSONObject pointJSON = new JSONObject();
		pointJSON.put("i", point.getI());
		pointJSON.put("j", point.getJ());
		pointJSON.put("k", point.getK());
		return pointJSON;
	}

	private Point pointFromJSON(JSONObject object) {
		return new Point(Double.parseDouble(object.get("i").toString()), Double.parseDouble(object.get("j").toString()), Double.parseDouble(object.get("k").toString()));
	}
}
