package tracks.singlePlayer.custom;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Map;

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
				sso.getGameScore() +
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
				observation.sqDist + "}";
	}

	public static JSONObject stateObservationToJSON(StateObservation sso) {
		JSONObject object = new JSONObject();
		object.put("gameScore", sso.getGameScore());
		object.put("gameTick", sso.getGameTick());
		object.put("gameWinner", sso.getGameWinner().toString());
		object.put("gameOver", sso.isGameOver());

		JSONArray worldDimension = new JSONArray();
		worldDimension.add(sso.getWorldDimension().width);
		worldDimension.add(sso.getWorldDimension().height);
		object.put("worldDimension", worldDimension);

		object.put("blockSize", sso.getBlockSize());
		object.put("noPlayers", sso.getNoPlayers());
		object.put("avatarSpeed", sso.getAvatarSpeed());

		JSONArray orientation = new JSONArray();
		orientation.add(sso.getAvatarOrientation().x);
		orientation.add(sso.getAvatarOrientation().y);
		object.put("avatarOrientation", orientation);

		JSONArray position = new JSONArray();
		position.add(sso.getAvatarPosition().x);
		position.add(sso.getAvatarPosition().y);
		object.put("avatarPosition", position);

		object.put("avatarLastAction", sso.getAvatarLastAction().toString());
		object.put("avatarType", sso.getAvatarType());
		object.put("avatarHealthPoints", sso.getAvatarHealthPoints());
		object.put("avatarMaxHealthPoints", sso.getAvatarMaxHealthPoints());
		object.put("avatarLimitHealthPoints", sso.getAvatarLimitHealthPoints());
		object.put("avatarAlive", sso.isAvatarAlive());

		JSONArray availableActions = new JSONArray();
		for (Types.ACTIONS action : sso.getAvailableActions())
			availableActions.add(action.toString());
		object.put("availableActions", availableActions);

		JSONArray avatarResources = new JSONArray();
		for (Map.Entry<Integer, Integer> entry : sso.getAvatarResources().entrySet()) {
			JSONObject entryObject = new JSONObject();
			entryObject.put(entry.getKey().toString(), entry.getValue());
		}
		object.put("avatarResources", avatarResources);

		JSONArray observationArray = new JSONArray();
		ArrayList<Observation>[][] observationGrid = sso.getObservationGrid();
		if (observationGrid != null) {
			for (int i = 0; i < observationGrid.length; i++) {
				for (int j = 0; j < observationGrid[i].length; j++) {
					for (Observation obs : observationGrid[i][j]) {
						observationArray.add(observationToJSON(obs));
					}
				}
			}
		}
		object.put("observations", observationArray);

		return object;
	}

	public static JSONObject observationToJSON(Observation observation) {
		JSONObject object = new JSONObject();
		object.put("category", observation.category);
		object.put("itype", observation.itype);
		object.put("obsId", observation.obsID);

		JSONArray position = new JSONArray();
		position.add(observation.position.x);
		position.add(observation.position.y);
		object.put("position", position);

		JSONArray reference = new JSONArray();
		reference.add(observation.reference.x);
		reference.add(observation.reference.y);
		object.put("reference", reference);

		object.put("sqDist", observation.sqDist);

		return object;
	}
}
