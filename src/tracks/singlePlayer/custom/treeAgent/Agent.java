package tracks.singlePlayer.custom.treeAgent;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import group.datagather.constants.Constants;
import ontology.Types;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.custom.JSONSaver;
import tracks.singlePlayer.custom.Utils;

import java.util.ArrayList;

public class Agent extends AbstractPlayer {

    private QuickSort quickSort;

    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        System.out.println("Agent constructor called");
        this.quickSort = new QuickSort();
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        JSONObject root = new JSONObject();
        this.explore(root, stateObs, 0);

        ArrayList<JSONObject> data = new ArrayList<>();
        data.add(root);

        System.out.println("starting saving");
        Thread thread = new Thread(new JSONSaver(data));
        thread.start();

        try {
            Thread.sleep(10000);
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void explore(JSONObject root, StateObservation stateObs, int depth) {
        JSONObject state = this.parseState(Utils.stateObservationToJSON(stateObs));

        root.put("state", state);
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {
            if (depth == 0) System.out.println("starting from the root again");
            JSONObject newRoot = new JSONObject();
            StateObservation stateObsCopy = stateObs.copy();
            stateObsCopy.advance(action);
            
            if (depth < 10) {
                root.put(action.toString(), newRoot);
                this.explore(newRoot, stateObsCopy, depth + 1);
            }
        }
    }

    private JSONObject parseState(JSONObject state) {
        JSONObject result = new JSONObject();

        double blockSize = Double.parseDouble(state.get("blockSize").toString());
        double width = Double.parseDouble(((JSONArray) state.get("worldDimension")).get(0).toString()) / blockSize;
        double height = Double.parseDouble(((JSONArray) state.get("worldDimension")).get(1).toString()) / blockSize;

        result.put("gameScore", Double.parseDouble(state.get("gameScore").toString()) / Constants.SCORE_NORMALISATION_FACTOR);
        result.put("avatarSpeed", Double.parseDouble(state.get("avatarSpeed").toString()) / Constants.SCORE_NORMALISATION_FACTOR);
        result.put("avatarHealthPoints", Double.parseDouble(state.get("avatarHealthPoints").toString()) / Constants.SCORE_NORMALISATION_FACTOR);
        result.put("avatarOrientation", state.get("avatarOrientation"));

        JSONArray position = (JSONArray) state.get("avatarPosition");
        JSONArray normalisedPosition = new JSONArray();
        normalisedPosition.add(Double.parseDouble(position.get(0).toString()) / blockSize / width);
        normalisedPosition.add(Double.parseDouble(position.get(1).toString()) / blockSize / height);
        result.put("avatarPosition", normalisedPosition);

        double x = Double.parseDouble(normalisedPosition.get(0).toString());
        double y = Double.parseDouble(normalisedPosition.get(1).toString());

        JSONArray observations = (JSONArray) state.get("observations");
        double[][] observationDistances = new double[observations.size()][2];
        double observationX, observationY;
        for (int i = 0; i < observationDistances.length; i++) {
            JSONArray observationPosition = (JSONArray) ((JSONObject) observations.get(i)).get("position");
            observationX = Double.parseDouble(observationPosition.get(0).toString()) / blockSize / width;
            observationY = Double.parseDouble(observationPosition.get(1).toString()) / blockSize / height;

            observationDistances[i][0] = i;
            observationDistances[i][1] = Math.pow(observationX - x, 2) + Math.pow(observationY - y, 2);
        }

        // sorting
        this.quickSort.sort(observationDistances);
        int size = Math.min(observationDistances.length, Constants.DV);

        JSONArray selectedObservations = new JSONArray();
        for (int i = 0; i < size; i++) {
            JSONObject temp = (JSONObject) observations.get((int) observationDistances[i][0]);
            temp.put("sqDist", observationDistances[i][1]);

            JSONArray tempPosition = (JSONArray) temp.get("position");
            tempPosition.set(0, Double.parseDouble(tempPosition.get(0).toString()) / blockSize / width);
            tempPosition.set(1, Double.parseDouble(tempPosition.get(1).toString()) / blockSize / height);
            temp.put("category", (Integer.parseInt(temp.get("category").toString()) + 1) / Constants.CATEGORY_NORMALISATION_FACTOR);
            temp.remove("itype");
            temp.remove("reference");
            temp.remove("obsId");
            selectedObservations.add(temp);
        }
        for (int i = size; i < Constants.DV; i++) {
            JSONObject temp = new JSONObject();
            temp.put("category", 0);
            temp.put("sqDist", 0);

            JSONArray tempPosition = new JSONArray();
            tempPosition.add(0);
            tempPosition.add(0);

            temp.put("position", tempPosition);

            selectedObservations.add(temp);
        }
        result.put("observations", selectedObservations);

        return result;
    }
}
