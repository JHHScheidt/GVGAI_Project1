package group.datagather.constants;

import serialization.Observation;
import serialization.SerializableStateObservation;

public class Utils {

	/**
	 * 	return "{" +
	 ", phase=" + phase +
	 ", gameScore=" + gameScore +
	 ", gameTick=" + gameTick +
	 ", gameWinner=" + gameWinner +
	 ", isGameOver=" + isGameOver +
	 ", worldDimension=" + java.util.Arrays.toString(worldDimension) +
	 ", blockSize=" + blockSize +
	 ", noOfPlayers=" + noOfPlayers +
	 ", avatarSpeed=" + avatarSpeed +
	 ", avatarOrientation=" + java.util.Arrays.toString(avatarOrientation) +
	 ", avatarPosition=" + java.util.Arrays.toString(avatarPosition) +
	 ", avatarLastAction=" + avatarLastAction +
	 ", avatarType=" + avatarType +
	 ", avatarHealthPoints=" + avatarHealthPoints +
	 ", avatarMaxHealthPoints=" + avatarMaxHealthPoints +
	 ", avatarLimitHealthPoints=" + avatarLimitHealthPoints +
	 ", isAvatarAlive=" + isAvatarAlive +
	 ", availableActions=" + availableActions +
	 ", avatarResources=" + avatarResources +
	 observation + "}";
	 */

	public static String serializableStateObservationToString(SerializableStateObservation sso) {
		Observation[][][] observationGrid = sso.getObservationGrid();

		String observation = "{";
		if (observationGrid != null) {
			for (int i = 0; i < observationGrid.length; i++) {
				for (int j = 0; j < observationGrid[i].length; j++) {
					for (Observation obs : observationGrid[i][j]) {
						observation += observationToString(obs) + "\n";
					}
				}
			}
		}
		observation += "}";

		return "{" +
				"," + sso.phase +
				"," + sso.gameScore +
				"," + sso.gameTick +
				"," + sso.gameWinner +
				"," + sso.isGameOver +
				"," + java.util.Arrays.toString(sso.worldDimension) +
				"," + sso.blockSize +
				"," + sso.noOfPlayers +
				"," + sso.avatarSpeed +
				"," + java.util.Arrays.toString(sso.avatarOrientation) +
				"," + java.util.Arrays.toString(sso.avatarPosition) +
				"," + sso.avatarLastAction +
				"," + sso.avatarType +
				"," + sso.avatarHealthPoints +
				"," + sso.avatarMaxHealthPoints +
				"," + sso.avatarLimitHealthPoints +
				"," + sso.isAvatarAlive +
				"," + sso.availableActions +
				"," + sso.avatarResources +
				"," + observation + "}";
	}

	public static String observationToString(Observation observation) {
		return "{" + observation.category + "," +
				observation.itype + "," +
				observation.obsID + "," +
				observation.position + "," +
				observation.reference + "," +
				observation.sqDist + "]";
	}
}
