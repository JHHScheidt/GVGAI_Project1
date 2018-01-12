package tracks.singlePlayer.custom.MaastCTS2;

import java.awt.Graphics2D;
import java.util.ArrayList;

import core.game.Game;
import ontology.Types;
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

public class Agent extends AbstractPlayer {
	public static IController controller;
	private boolean firstMove;
	private ArrayList<JSONObject> data;
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
		this.firstMove = true;
		Constants.AGENT_ID = AgentId;
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
		this.firstMove = true;
		Constants.AGENT_ID = AgentId;
	}
	
	public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer, IController controller){
		Agent.controller = controller;
		controller.init(so,  elapsedTimer);

		this.data = new ArrayList<>();
		this.firstMove = true;
		Constants.AGENT_ID = AgentId;
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		JSONObject object = new JSONObject();
		JSONObject state = Utils.stateObservationToJSON(stateObs);
		object.put("state", state);
		Globals.knowledgeBase.update(stateObs);

		// decide action
		Types.ACTIONS result = controller.chooseAction(stateObs, elapsedTimer);

		// store new data and set new state of previous action
		object.put("action", result.toString());
		this.data.add(object);
		if (!this.firstMove) {
			this.data.get(this.data.size() - 1).put("newState", state);
		}
		this.firstMove = false;


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

}
