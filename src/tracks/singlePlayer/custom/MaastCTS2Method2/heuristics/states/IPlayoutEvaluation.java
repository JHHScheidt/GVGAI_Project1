package tracks.singlePlayer.custom.MaastCTS2Method2.heuristics.states;

import core.game.StateObservation;
import tracks.singlePlayer.custom.MaastCTS2Method2.test.IPrintableConfig;

public interface IPlayoutEvaluation extends IPrintableConfig {
	public double scorePlayout(StateObservation stateObs);
}
