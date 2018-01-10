package group.learning.q.octree;

public class Point {

	private double i, j, k;

	public Point(double i, double j, double k) {
		this.i = i;
		this.j = j;
		this.k = k;
	}

	public double getI() {
		return this.i;
	}

	public double getJ() {
		return this.j;
	}

	public double getK() {
		return this.k;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Point) {
			Point other = (Point) o;
			if (other.i == this.i && other.j == this.j && other.k == this.k) return true;
			else return false;
		} else return false;
	}

	@Override
	public String toString() {
		return this.i + " " + this.j + " " + this.k;
	}
}
