package tracks.singlePlayer.custom;

import ontology.Types;

public class Tuple {

	private String state, newState;
	private Types.ACTIONS action;

	public Tuple(String state, Types.ACTIONS action, String newState) {
		this.state = state;
		this.action = action;
		this.newState = newState;
	}

	public String getState() {
		return this.state;
	}

	public String getNewState() {
		return this.newState;
	}

	public Types.ACTIONS getAction() {
		return this.action;
	}

	public void setNewState(String newState) {
		this.newState = newState;
	}

	public String toString() {
		if (this.newState != null) return "{" + this.state.toString() + "," + this.action.toString() + "," + this.newState.toString() + "}";
		else return "{" + this.state.toString() + "," + this.action.toString() + "}";
	}
}

