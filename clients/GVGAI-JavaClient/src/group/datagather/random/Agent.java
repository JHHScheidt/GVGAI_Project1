package group.datagather.random;

import group.datagather.constants.Constants;
import group.datagather.constants.DataSaver;
import group.datagather.constants.Tuple;
import group.datagather.constants.Utils;
import serialization.SerializableStateObservation;
import serialization.Types;
import utils.AbstractPlayer;
import utils.ElapsedCpuTimer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class Agent extends AbstractPlayer {

	/**
	 * getImmovablePositions() array of itype mapped to array of observations, itype = type of sprite, observations in this list are e.g. walls
	 * getMovablePositions() array of itype mapped to array of bservations, observations in here are e.g. pushable objects / collectables
	 */

	private ArrayList<Tuple> data;

	private Random random;

	private boolean firstMove;

	public Agent() {
		this.lastSsoType = Types.LEARNING_SSO_TYPE.JSON;

		File directory = new File(Constants.OUTPUT_DIR);
		if (!directory.exists()) directory.mkdirs();

		this.data = new ArrayList<>();
		this.random = new Random(System.nanoTime());
	}

	@Override
	public void init(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
		this.firstMove = true;
	}

	@Override
	public Types.ACTIONS act(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
		// get the current state
		String state = Utils.serializableStateObservationToString(sso);

		// decide action
		Types.ACTIONS action = sso.getAvailableActions().get(this.random.nextInt(sso.getAvailableActions().size()));

		// store new data and set new state of previous action
		Tuple dataPoint = new Tuple(state, action, null);
		if (!this.firstMove) this.data.get(this.data.size() - 1).setNewState(state);
		this.data.add(dataPoint);

		return action;
	}

	@Override
	public int result(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
		Thread thread = new Thread(new DataSaver(this.data));
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
		return Constants.CURRENT_LEVEL_ID;
	}

}
