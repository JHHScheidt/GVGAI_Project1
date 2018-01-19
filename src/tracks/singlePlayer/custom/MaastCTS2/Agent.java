package tracks.singlePlayer.custom.MaastCTS2;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.model.annotation.Quick;
import core.game.Game;
import ontology.Types;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import tracks.singlePlayer.custom.Constants;
import tracks.singlePlayer.custom.JSONSaver;
import tracks.singlePlayer.custom.MaastCTS2.controller.IController;
import tracks.singlePlayer.custom.MaastCTS2.controller.MctsController;
import tracks.singlePlayer.custom.MaastCTS2.heuristics.states.GvgAiEvaluation;
import tracks.singlePlayer.custom.MaastCTS2.heuristics.states.IPlayoutEvaluation;
import tracks.singlePlayer.custom.MaastCTS2.move_selection.IMoveSelectionStrategy;
import tracks.singlePlayer.custom.MaastCTS2.move_selection.MaxAvgScore;
import tracks.singlePlayer.custom.MaastCTS2.playout.IPlayoutStrategy;
import tracks.singlePlayer.custom.MaastCTS2.playout.NstPlayout;
import tracks.singlePlayer.custom.MaastCTS2.selection.ISelectionStrategy;
import tracks.singlePlayer.custom.MaastCTS2.selection.ol.ProgressiveHistory;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.custom.Utils;
import tracks.singlePlayer.custom.treeAgent.QuickSort;

public class Agent extends AbstractPlayer {

	public static IController controller;
	private ArrayList<JSONObject> data;
	private QuickSort quickSort;
	private int AgentId = 1;

	/**
	 * constructor for competition
	 * 
	 * @param so
	 * @param elapsedTimer
	 */
	public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
		MctsController.TIME_BUFFER_MILLISEC = 8;	// shorter time buffer because better hardware on official competition server
		controller = new MctsController(new ProgressiveHistory(0.6, 1.0), new NstPlayout(10, 0.5, 7.0, 3), 
				new MaxAvgScore(), new GvgAiEvaluation(), true, true, true, true, true, 0.6, 3, true, false);
		controller.init(so, elapsedTimer);

		this.data = new ArrayList<>();
		this.quickSort = new QuickSort();
		Constants.AGENT_ID = this.AgentId;
	}

	public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer,
			ISelectionStrategy selectionStrategy, IPlayoutStrategy playoutStrategy, 
			IMoveSelectionStrategy moveSelectionStrategy, IPlayoutEvaluation playoutEval,
			boolean initBreadthFirst, boolean noveltyBasedPruning, boolean exploreLosses,
			boolean knowledgeBasedEval, boolean detectDeterministicGames, boolean treeReuse,
			double treeReuseGamma, int maxNumSafetyChecks, boolean alwaysKB, boolean noTreeReuseBFTI) {
		controller = new MctsController(selectionStrategy, playoutStrategy, moveSelectionStrategy, 
										playoutEval, initBreadthFirst, noveltyBasedPruning, exploreLosses,
										knowledgeBasedEval, treeReuse, treeReuseGamma, maxNumSafetyChecks, alwaysKB, noTreeReuseBFTI);
		controller.init(so, elapsedTimer);

		this.data = new ArrayList<>();
		this.quickSort = new QuickSort();
		Constants.AGENT_ID = AgentId;
	}
	
	public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer, IController controller){
		Agent.controller = controller;
		controller.init(so,  elapsedTimer);

		this.data = new ArrayList<>();
		this.quickSort = new QuickSort();
		Constants.AGENT_ID = AgentId;
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		JSONObject object = new JSONObject();
		JSONObject state = this.parseState(Utils.stateObservationToJSON(stateObs));
		object.put("state", state);
		Globals.knowledgeBase.update(stateObs);

		// decide action
		Types.ACTIONS result = controller.chooseAction(stateObs, elapsedTimer);

		// store new data and set new state of previous action
		object.put("action", result.toString());
		this.data.add(object);

		//... and return it.
		return result;

	}
	
	@Override
	public void result(StateObservation stateObservation, ElapsedCpuTimer elapsedCpuTimer){
		controller.result(stateObservation, elapsedCpuTimer);
    }
	
	@Override
	public void draw(Graphics2D g){
		if(Globals.DEBUG_DRAW){
			Globals.knowledgeBase.draw(g);
		}
	}
	/**
	 * Closes the agent, writing actions to file.
	 */
	@Override
	public void teardown(Game played) {
		Thread thread = new Thread(new JSONSaver(this.data));
		thread.start();
		this.data = new ArrayList<>();

		Constants.CURRENT_LEVEL_ID++;
		if(Constants.CURRENT_GAME_ITER<50 && Constants.CURRENT_LEVEL_ID == 5) {
			Constants.CURRENT_GAME_ITER++;
			Constants.CURRENT_LEVEL_ID = 0;
			System.out.println(Constants.CURRENT_GAME_ITER);
		}
		else if(Constants.CURRENT_GAME_ITER == 50 && Constants.CURRENT_LEVEL_ID == 5) {
			while (thread.isAlive()){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.exit(0);
		}

		super.teardown(played);
	}


	private JSONObject parseState(JSONObject state) {
		JSONObject result = new JSONObject();

		double blockSize = Double.parseDouble(state.get("blockSize").toString());
		double width = Double.parseDouble(((JSONArray) state.get("worldDimension")).get(0).toString()) / blockSize;
		double height = Double.parseDouble(((JSONArray) state.get("worldDimension")).get(1).toString()) / blockSize;

		result.put("gameScore", Double.parseDouble(state.get("gameScore").toString()) / group.datagather.constants.Constants.SCORE_NORMALISATION_FACTOR);
		result.put("avatarSpeed", Double.parseDouble(state.get("avatarSpeed").toString()) / group.datagather.constants.Constants.SCORE_NORMALISATION_FACTOR);
		result.put("avatarHealthPoints", Double.parseDouble(state.get("avatarHealthPoints").toString()) / group.datagather.constants.Constants.SCORE_NORMALISATION_FACTOR);
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
		int size = Math.min(observationDistances.length, group.datagather.constants.Constants.DV);

		JSONArray selectedObservations = new JSONArray();
		for (int i = 0; i < size; i++) {
			JSONObject temp = (JSONObject) observations.get((int) observationDistances[i][0]);
			temp.put("sqDist", observationDistances[i][1]);

			JSONArray tempPosition = (JSONArray) temp.get("position");
			tempPosition.set(0, Double.parseDouble(tempPosition.get(0).toString()) / blockSize / width);
			tempPosition.set(1, Double.parseDouble(tempPosition.get(1).toString()) / blockSize / height);
			temp.put("category", (Integer.parseInt(temp.get("category").toString()) + 1) / group.datagather.constants.Constants.CATEGORY_NORMALISATION_FACTOR);
			temp.remove("itype");
			temp.remove("reference");
			temp.remove("obsId");
			selectedObservations.add(temp);
		}
		for (int i = size; i < group.datagather.constants.Constants.DV; i++) {
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
