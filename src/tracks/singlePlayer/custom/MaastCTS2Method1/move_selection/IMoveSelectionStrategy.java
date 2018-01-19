package tracks.singlePlayer.custom.MaastCTS2Method1.move_selection;

import ontology.Types.ACTIONS;
import tracks.singlePlayer.custom.MaastCTS2Method1.model.MctNode;
import tracks.singlePlayer.custom.MaastCTS2Method1.test.IPrintableConfig;

public interface IMoveSelectionStrategy extends IPrintableConfig {
	
	/** Should be implemented to select a move to play in the real game for the position in the given root */
	public ACTIONS selectMove(MctNode root);

}
