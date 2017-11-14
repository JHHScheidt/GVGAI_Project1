package group.datagather.random;

import group.datagather.constants.Constants;
import serialization.SerializableStateObservation;
import serialization.Types;
import utils.AbstractPlayer;
import utils.ElapsedCpuTimer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Agent extends AbstractPlayer {

	/**
	 * getImmovablePositions() array of itype mapped to array of observations, itype = type of sprite, observations in this list are e.g. walls
	 * getMovablePositions() array of itype mapped to array of bservations, observations in here are e.g. pushable objects / collectables
	 */

	private File outputFile;

	public Agent() {
		File directory = new File(Constants.OUTPUT_DIR);
		if (!directory.exists()) directory.mkdirs();
		this.createOutputFile();
	}

	@Override
	public void init(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {

	}

	boolean printed = false;
	@Override
	public Types.ACTIONS act(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
//		System.out.println(sso.getFromAvatarSpritesPositions().length);
		if (!printed) {
//			for (int i = 0; i < sso.getImmovablePositions().length; i++) {
//				for (Observation o : sso.getImmovablePositions()[i])
//					System.out.println(o);
//			}
			try {
				PrintWriter writer = new PrintWriter(new FileOutputStream(this.outputFile));
				writer.println(sso.toString());
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
//		System.out.println(sso.getMovablePositions().length);
//		System.out.println(sso.getResourcesPositions().length);

		return sso.getAvailableActions().get(0);
	}

	@Override
	public int result(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer) {
		Constants.CURRENT_LEVEL_ID++;
		this.createOutputFile();
		return 0;
	}

	private void createOutputFile() {
		this.outputFile = new File(Constants.OUTPUT_DIR + Constants.CURRENT_GAME_ID + "_" + Constants.CURRENT_LEVEL_ID + ".txt");
	}

	private void saveData() {

	}
}
