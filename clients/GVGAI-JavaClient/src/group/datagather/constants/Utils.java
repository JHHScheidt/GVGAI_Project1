package group.datagather.constants;

import serialization.Observation;
import serialization.SerializableStateObservation;

public class Utils {

	public String serializableStateObservationToString(SerializableStateObservation sso) {
		String observation = "ObservationGrid{\n";
		if (observationGrid != null) {
			for (int i = 0; i < observationGrid.length; i++) {
				for (int j = 0; j < observationGrid[i].length; j++) {
					for (Observation obs : observationGrid[i][j]) {
						observation += obs.toString();
					}
				}
			}
		}
		observation += "}";

		return "SerializableStateObservation{" +
				"imageArray=" + java.util.Arrays.toString(imageArray) +
				", phase=" + phase +
				", isValidation=" + isValidation +
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
				", NPCPositions=" + java.util.Arrays.toString(NPCPositions) +
				", immovablePositions=" + java.util.Arrays.toString(immovablePositions) +
				", movablePositions=" + java.util.Arrays.toString(movablePositions) +
				", resourcesPositions=" + java.util.Arrays.toString(resourcesPositions) +
				", portalsPositions=" + java.util.Arrays.toString(portalsPositions) +
				", fromAvatarSpritesPositions=" + java.util.Arrays.toString(fromAvatarSpritesPositions) +
				"}\n" + observation;
	}
}
