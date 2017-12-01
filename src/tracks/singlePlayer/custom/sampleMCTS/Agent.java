package tracks.singlePlayer.custom.sampleMCTS;

import core.game.Game;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import org.json.simple.JSONObject;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.custom.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is an implementation of MCTS UCT
 */
public class Agent extends AbstractPlayer {

    private boolean firstMove;
    private ArrayList<JSONObject> data;

    public int num_actions;
    public Types.ACTIONS[] actions;

    protected SingleMCTSPlayer mctsPlayer;

    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        //Get the actions in a static array.
        ArrayList<Types.ACTIONS> act = so.getAvailableActions();
        actions = new Types.ACTIONS[act.size()];
        for(int i = 0; i < actions.length; ++i)
        {
            actions[i] = act.get(i);
        }
        num_actions = actions.length;

        //Create the player.

        mctsPlayer = getPlayer(so, elapsedTimer);

        this.data = new ArrayList<>();
        this.firstMove = true;
    }

    public SingleMCTSPlayer getPlayer(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        return new SingleMCTSPlayer(new Random(), num_actions, actions);
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        // get the current state

        JSONObject object = new JSONObject();
        JSONObject state = Utils.stateObservationToJSON(stateObs);
        object.put("state", state);

        //-------------------THE THING----------------
        //Set the state observation object as the new root of the tree.
        mctsPlayer.init(stateObs);

        //Determine the action using MCTS...
        int action = mctsPlayer.run(elapsedTimer);
        //-------------------THE THING----------------


        // decide action
        Types.ACTIONS result = actions[action];

        // store new data and set new state of previous action
        object.put("action", result);
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
