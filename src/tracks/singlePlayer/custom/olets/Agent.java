package tracks.singlePlayer.custom.olets;

import core.game.Game;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import org.json.simple.JSONObject;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.custom.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Code written by Adrien Couetoux, acouetoux@ulg.ac.be.
 * Date: 15/12/2015
 * @author Adrien Couëtoux
 */

public class Agent extends AbstractPlayer {


    private boolean firstMove;
    private ArrayList<JSONObject> data;
    /**
     * Number of feasible actions (usually from 2 to 5)
     */
    public int NUM_ACTIONS;
    /**
     * Feasible actions array, of length NUM_ACTIONS
     */
    public Types.ACTIONS[] actions;
    /**
     * The Monte Carlo Tree Search agent - the core of the algorithm
     */
    public SingleMCTSPlayer mctsPlayer;

    /**
     * Public constructor with state observation and time due.
     *
     * @param so           state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        //Get the actions in a static array.
        ArrayList<Types.ACTIONS> act = so.getAvailableActions();
        actions = new Types.ACTIONS[act.size()];
        for (int i = 0; i < actions.length; ++i) {
            actions[i] = act.get(i);
        }
        NUM_ACTIONS = actions.length;

        //Create the player.
        mctsPlayer = new SingleMCTSPlayer(new Random(), this);
        this.data = new ArrayList<>();
        this.firstMove = true;
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     *
     * @param stateObs     Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        JSONObject object = new JSONObject();
        JSONObject state = Utils.stateObservationToJSON(stateObs);
        object.put("state", state);

        //Set the state observation object as the new root of the tree.
        mctsPlayer.init(stateObs);

        //Determine the action using MCTS...
        int action = mctsPlayer.run(elapsedTimer);
        Types.ACTIONS result = actions[action];

        object.put("action", result.toString());
        this.data.add(object);
        if (!this.firstMove) {
            this.data.get(this.data.size() - 1).put("newState", state);
        }
        this.firstMove = false;
        //... and return it.

        return result;
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
