package group.datagather;

import group.datagather.constants.Constants;

public class Input {

	private double gameScore, avatarSpeed, avatarHealthPoints, avatarOrientationX, avatarOrientationY, avatarPositionX, avatarPositionY;
	private double action;
	private double[][] observations;
	private int observationId = 0;

	public Input() {
		this.observations = new double[Constants.DV][4];
	}

	public void addObservation(double distance, double category, double x, double y) {
		double[] observation = this.observations[this.observationId++];
		observation[0] = distance;
		observation[1] = category;
		observation[2] = x;
		observation[3] = y;
	}

	public double getGameScore() {
		return gameScore;
	}

	public double getAvatarSpeed() {
		return avatarSpeed;
	}

	public double getAvatarHealthPoints() {
		return avatarHealthPoints;
	}

	public double getAvatarOrientationX() {
		return avatarOrientationX;
	}

	public double getAvatarOrientationY() {
		return avatarOrientationY;
	}

	public double getAvatarPositionX() {
		return avatarPositionX;
	}

	public double getAvatarPositionY() {
		return avatarPositionY;
	}

	public double getAction() {
		return action;
	}

	public double[][] getObservations() {
		return observations;
	}

	public void setGameScore(double gameScore) {
		this.gameScore = gameScore;
	}

	public void setAvatarSpeed(double avatarSpeed) {
		this.avatarSpeed = avatarSpeed;
	}

	public void setAvatarHealthPoints(double avatarHealthPoints) {
		this.avatarHealthPoints = avatarHealthPoints;
	}

	public void setAvatarOrientationX(double avatarOrientationX) {
		this.avatarOrientationX = avatarOrientationX;
	}

	public void setAvatarOrientationY(double avatarOrientationY) {
		this.avatarOrientationY = avatarOrientationY;
	}

	public void setAvatarPositionX(double avatarPositionX) {
		this.avatarPositionX = avatarPositionX;
	}

	public void setAvatarPositionY(double avatarPositionY) {
		this.avatarPositionY = avatarPositionY;
	}

	public void setAction(double action) {
		this.action = action;
	}
}
