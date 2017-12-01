package tracks.singlePlayer.custom;

import core.game.Observation;
import core.game.StateObservation;

import java.util.ArrayList;

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

	public static String serializableStateObservationToString(StateObservation sso) {
		ArrayList<Observation>[][] observationGrid = sso.getObservationGrid();

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
				"," + sso.getGameScore() +
				"," + sso.getGameTick() +
				"," + sso.getGameWinner() +
				"," + sso.isGameOver() +
				",[" + sso.getWorldDimension().getHeight() + "," + sso.getWorldDimension().getWidth() +
				"]," + sso.getBlockSize() +
				"," + sso.getNoPlayers() +
				"," + sso.getAvatarSpeed() +
				"," + sso.getAvatarOrientation().toString() +
				"," + sso.getAvatarPosition().toString() +
				"," + sso.getAvatarLastAction()+
				"," + sso.getAvatarType() +
				"," + sso.getAvatarHealthPoints() +
				"," + sso.getAvatarMaxHealthPoints() +
				"," + sso.getAvatarLimitHealthPoints() +
				"," + sso.isAvatarAlive() +
				"," + sso.getAvailableActions() +
				"," + sso.getAvatarResources() +
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
