package group.datagather.constants;

		import serialization.Observation;
		import serialization.SerializableStateObservation;

public class Utils {

	public static String serializableStateObservationToString(SerializableStateObservation sso) {
		Observation[][][] observationGrid = sso.getObservationGrid();

		String observation = "";
		if (observationGrid != null) {
			for (int i = 0; i < observationGrid.length; i++) {
				for (int j = 0; j < observationGrid[i].length; j++) {
					for (Observation obs : observationGrid[i][j]) {
						observation += observationToString(obs) + "\n";
					}
				}
			}
		}

		return "{" + sso.gameScore +
				";" + sso.gameTick +
				";" + sso.gameWinner +
				";" + sso.isGameOver +
				";" + java.util.Arrays.toString(sso.worldDimension) +
				";" + sso.blockSize +
				";" + sso.avatarSpeed +
				";" + java.util.Arrays.toString(sso.avatarOrientation) +
				";" + java.util.Arrays.toString(sso.avatarPosition) +
				";" + sso.avatarLastAction +
				";" + sso.avatarType +
				";" + sso.avatarHealthPoints +
				";" + sso.avatarMaxHealthPoints +
				";" + sso.avatarLimitHealthPoints +
				";" + sso.isAvatarAlive +
				";" + sso.availableActions +
				";" + sso.avatarResources +
				";\n" + observation + "}";
	}

	public static String observationToString(Observation observation) {
		return "[" + observation.category + "," +
				observation.itype + "," +
				observation.obsID + "," +
				observation.position + "," +
				observation.reference + "," +
				observation.sqDist + "]";
	}
}
