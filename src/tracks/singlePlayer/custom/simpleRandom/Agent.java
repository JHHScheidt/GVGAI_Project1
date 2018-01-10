package tracks.singlePlayer.custom.simpleRandom;

import core.game.Game;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import org.json.simple.JSONObject;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.custom.Constants;
import tracks.singlePlayer.custom.JSONSaver;
import tracks.singlePlayer.custom.Utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {
    private boolean firstMove;
    private ArrayList<JSONObject> data;
    /**
     * Random generator for the agent.
     */
    protected Random randomGenerator;
    /**
     * List of available actions for the agent
     */
    protected ArrayList<Types.ACTIONS> actions;


    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        randomGenerator = new Random();
        actions = so.getAvailableActions();
        this.data = new ArrayList<>();
        this.firstMove = true;
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        JSONObject object = new JSONObject();
        JSONObject state = Utils.stateObservationToJSON(stateObs);
        object.put("state", state);
        int index = randomGenerator.nextInt(actions.size());
        // store new data and set new state of previous action

        object.put("action", actions.get(index).toString());
        this.data.add(object);
        if (!this.firstMove) {
            this.data.get(this.data.size() - 1).put("newState", state);
        }
        this.firstMove = false;
        return actions.get(index);
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
