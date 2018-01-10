package tracks.singlePlayer.advanced.MaastCTS2.heuristics.states;

import tracks.singlePlayer.advanced.MaastCTS2.test.IPrintableConfig;
import core.game.StateObservation;

public interface IPlayoutEvaluation extends IPrintableConfig {
	public double scorePlayout(StateObservation stateObs);
}
