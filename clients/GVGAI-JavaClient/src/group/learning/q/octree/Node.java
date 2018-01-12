package group.learning.q.octree;

public class Node {

	private boolean leafNode;
	private Node[] children;
	private Point point;
	private double value;

	public Node(Point point) {
		this.leafNode = true;
		this.point = point;
		this.value = Math.random() * 0.1 - 0.05;
	}

	/**
	 * Supposed to be only used for reading in the tree
	 */
	public Node() {
		this.leafNode = true;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Node getChild(int id) {
		if (this.children == null) return null;
		else return this.children[id];
	}

	public void setChild(int id, Node child) {
		if (this.children == null) this.children = new Node[8];
		this.children[id] = child;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void addValue(double value) {
		this.value += value;
	}

	public Point getPoint() {
		return this.point;
	}

	public Point getAndRemovePoint() {
		this.leafNode = false;
		Point point = this.point;
		this.point = null;
		return point;
	}

	public boolean isLeafNode() {
		return this.leafNode;
	}
}
