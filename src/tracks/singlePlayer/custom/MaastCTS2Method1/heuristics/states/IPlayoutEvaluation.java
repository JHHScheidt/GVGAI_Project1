package tracks.singlePlayer.custom.MaastCTS2Method1.heuristics.states;

import tracks.singlePlayer.custom.MaastCTS2Method1.test.IPrintableConfig;
import core.game.StateObservation;

public interface IPlayoutEvaluation extends IPrintableConfig {
	public double scorePlayout(StateObservation stateObs);
}
