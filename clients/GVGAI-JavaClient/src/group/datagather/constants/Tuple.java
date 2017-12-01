package group.datagather.constants;

import serialization.Types;

public class Tuple {

	private String state;
	private Types.ACTIONS action;

	public Tuple(String state, Types.ACTIONS action) {
		this.state = state;
		this.action = action;
	}

	public String getState() {
		return this.state;
	}

	public Types.ACTIONS getAction() {
		return this.action;
	}

	public String toString() {
		return "{" + this.state + "," + this.action + "}";
	}
}

