package main;

/**
 * A class used to keep track of the current game mode across multiple threads.
 * 
 * @author Ben Patterson
 */
public class CurrentMode {

	private GZmode mode;

	public CurrentMode(GZmode mode) {
		this.mode = mode;
	}

	public synchronized void setMode(GZmode mode) {
		this.mode = mode;
	}

	public synchronized GZmode getMode() {
		return mode;
	}

}
