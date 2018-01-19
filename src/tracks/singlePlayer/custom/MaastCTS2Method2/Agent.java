package tracks.singlePlayer.custom.MaastCTS2Method2;

import core.game.Game;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import org.json.simple.JSONObject;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.custom.Constants;
import tracks.singlePlayer.custom.JSONSaver;
import tracks.singlePlayer.custom.MaastCTS2Method2.Server.Server;
import tracks.singlePlayer.custom.MaastCTS2Method2.controller.IController;
import tracks.singlePlayer.custom.MaastCTS2Method2.controller.MctsController;
import tracks.singlePlayer.custom.MaastCTS2Method2.heuristics.states.GvgAiEvaluation;
import tracks.singlePlayer.custom.MaastCTS2Method2.heuristics.states.IPlayoutEvaluation;
import tracks.singlePlayer.custom.MaastCTS2Method2.move_selection.IMoveSelectionStrategy;
import tracks.singlePlayer.custom.MaastCTS2Method2.move_selection.MaxAvgScore;
import tracks.singlePlayer.custom.MaastCTS2Method2.playout.IPlayoutStrategy;
import tracks.singlePlayer.custom.MaastCTS2Method2.playout.NstPlayout;
import tracks.singlePlayer.custom.MaastCTS2Method2.selection.ISelectionStrategy;
import tracks.singlePlayer.custom.MaastCTS2Method2.selection.NNSelction;
import tracks.singlePlayer.custom.MaastCTS2Method2.selection.ol.ProgressiveHistory;
import tracks.singlePlayer.custom.Utils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Agent extends AbstractPlayer {
	public static IController controller;
	private ArrayList<JSONObject> data;
	private Server server;

	/**
	 * constructor for competition
	 * 
	 * @param so
	 * @param elapsedTimer
	 */
	public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
		MctsController.TIME_BUFFER_MILLISEC = 8;	// shorter time buffer because better hardware on official competition server
		controller = new MctsController(new NNSelction(), new NstPlayout(10, 0.5, 7.0, 3),
				new MaxAvgScore(), new GvgAiEvaluation(), true, true, true, true, true, 0.6, 3, true, false);

		try {
			System.out.print("connect servers");
			server= new Server(8080);
		}catch (IOException e ){
			System.out.print("Connection error");
		}
		controller.init(so, elapsedTimer, server);

		this.data = new ArrayList<>();
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

		try {
			server= new Server(8080);
		}catch (IOException e ){
			System.out.print("Connection error");
		}
		controller.init(so, elapsedTimer, server);
		this.data = new ArrayList<>();
	}
	
	public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer, IController controller){
		Agent.controller = controller;

		try {
			server= new Server(8080);
		}catch (IOException e ){
			System.out.print("Connection error");
		}
		controller.init(so, elapsedTimer, server);
		this.data = new ArrayList<>();
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		//JSONObject object = new JSONObject();
		//JSONObject state = Utils.stateObservationToJSON(stateObs);
		//object.put("state", state);
		Globals.knowledgeBase.update(stateObs);

		// decide action
		ACTIONS result = controller.chooseAction(stateObs, elapsedTimer);
		server.trainOn(stateObs, result);
		// store new data and set new state of previous action
		//object.put("action", result.toString());
		//this.data.add(object);

		System.out.println("Executed action: " + result);
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
		if (Constants.CURRENT_LEVEL_ID == 5) {
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
