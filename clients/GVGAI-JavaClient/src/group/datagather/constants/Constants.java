package group.datagather.constants;

public class Constants {

	/**
	 * Directory constants
	 */
	public static final String RAW_OUTPUT_DIR = "res/data/raw/";
	public static final String PREPROCESSED_OUTPUT_DIR = "res/data/preprocessed/";
	public static final String NETWORK_WEIGHTS_DIR = "res/network/";

	/**
	 * Constants for deictic view parsing
	 */
	public static final double CATEGORY_NORMALISATION_FACTOR = 8;
	public static final double SCORE_NORMALISATION_FACTOR = 10000;
	public static final double AVAILABLE_ACTIONS = 5;
	public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3, USE = 4;
	public static final int DV = 10;
}
