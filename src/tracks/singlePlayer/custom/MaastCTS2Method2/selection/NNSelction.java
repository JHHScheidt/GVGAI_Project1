package tracks.singlePlayer.custom.MaastCTS2Method2.selection;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import tracks.singlePlayer.custom.MaastCTS2Method2.Globals;
import tracks.singlePlayer.custom.MaastCTS2Method2.KnowledgeBase;
import tracks.singlePlayer.custom.MaastCTS2Method2.Server.Server;
import tracks.singlePlayer.custom.MaastCTS2Method2.controller.MctsController;
import tracks.singlePlayer.custom.MaastCTS2Method2.model.MctNode;
import tracks.singlePlayer.custom.MaastCTS2Method2.model.StateObs;

import java.util.ArrayList;
import java.util.HashMap;

public class NNSelction implements ISelectionStrategy {


    public NNSelction() {
    }

    /**/
    @Override
    public MctNode select(MctNode rootNode, ElapsedCpuTimer timer, Server server) {
        MctNode node = rootNode;
        StateObservation state = rootNode.getStateObs();

        HashMap<Integer, Integer> previousResources = state.getAvatarResources();
        HashMap<Integer, Integer> nextResources;

        StateObs stateObs = new StateObs(state, true);

        boolean firstStateGenerated = false;
        // use uct to select child
        while (!state.isGameOver() && node.isFullyExpanded() && !node.getChildren().isEmpty()) {
            double previousScore = state.getGameScore();
            int previousNumEvents = state.getEventsHistory().size();
            Vector2d previousAvatarPos = state.getAvatarPosition();
            Vector2d previousAvatarOrientation = state.getAvatarOrientation();

            node.preSelect(state);
            //added by tr not tested and not sure if this is right
            ArrayList<MctNode> children = node.getChildren();
            ArrayList<Integer> possibleActions= new ArrayList<>();
            for (int i = 0; i < children.size(); i++) {
                System.out.println("Action:"+ children.get(i).getActionFromParent());
                possibleActions.add(getActionIdx(children.get(i).getActionFromParent()));
            }
            Types.ACTIONS action = server.getActionFromNN(node, possibleActions);
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i).getActionFromParent() == action)
                    node = children.get(i);
            }

            stateObs = node.generateNewStateObs(stateObs, node.getActionFromParent());
            state = stateObs.getStateObsNoCopy();

            if (!firstStateGenerated) {
                firstStateGenerated = true;
                KnowledgeBase kb = Globals.knowledgeBase;
                int pheromoneStrength = kb.getPheromoneStrength(kb.positionToCell(state.getAvatarPosition()));

                if (pheromoneStrength > 0) {
                    MctsController.ONE_STEP_EVAL -= 0.005 * pheromoneStrength;
                }

                if (node.getActionFromParent() == Types.ACTIONS.ACTION_USE) {
                    // slight punishment for using the USE action
                    // (only want to use this action when it serves an observable purpose)
                    MctsController.ONE_STEP_EVAL -= 0.01;
                }
            }

            nextResources = state.getAvatarResources();
            Globals.knowledgeBase.addEventKnowledge(previousScore, previousNumEvents, previousAvatarPos,
                    previousAvatarOrientation, node.getActionFromParent(), state,
                    previousResources, nextResources, false);
            previousResources = nextResources;
        }

        return node;
    }

    public int getActionIdx(Types.ACTIONS action){
        switch (action){
            case ACTION_UP:
                return 0;
            case ACTION_DOWN:
                return 1;
            case ACTION_LEFT:
                return 2;
            case ACTION_RIGHT:
                return 3;
            case ACTION_USE:
                return 4;
            default:
                return 5;
        }
    }
    public Types.ACTIONS getActionEnum(int action){
        switch (action){
            case 0:
                return Types.ACTIONS.ACTION_UP;
            case 1:
                return Types.ACTIONS.ACTION_DOWN;
            case 2:
                return Types.ACTIONS.ACTION_LEFT;
            case 3:
                return Types.ACTIONS.ACTION_RIGHT;
            case 4:
                return Types.ACTIONS.ACTION_USE;
            default:
                return Types.ACTIONS.ACTION_NIL;
        }
    }

    //private MctNode getNextNode(MctNode node) {
    //}

    @Override
    public int getDesiredActionNGramSize() {
        return -1;
    }

    @Override
    public String getName() {
        return "neural_network";
    }

    @Override
    public String getConfigDataString() {
        return "NN";
    }

    @Override
    public void init(StateObservation so, ElapsedCpuTimer elapsedTimer) {
    }

    @Override
    public boolean wantsActionStatistics() {
        return true;
    }

}

