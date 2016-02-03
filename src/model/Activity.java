package model;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Activity extends Observable {

	private static final Logger lWin = Logger.getLogger(Activity.class.getName());
	private String process, windowTitle;
	private State state, oldState;

	public Activity(State userState, String process, String windowTitle) {
		this.process = process;
		this.windowTitle = windowTitle;
		this.state = userState;
	}

	public enum State {
		UNKNOWN, ACTIVE, IDLE, AWAY
	};

	public void change(State userState, String process, String windowTitle) {
		setUserState(userState);
		setProcess(process);
		setWindowTitle(windowTitle);
		lWin.log(Level.INFO, "Current process: {1} | Title: {2}",
				new Object[] { userState.toString().toLowerCase(), process, windowTitle });
		setChanged();
		notifyObservers(process);
	}

	public void change(State userState) {
		setUserState(userState);
		lWin.log(Level.INFO, "Status: {0}", new Object[] { userState.toString().toLowerCase(), "", "" });
		setChanged();
		notifyObservers(userState);
	}

	// ------------------ GETTER + SETTER --------------------------

	public String getWindowTitle() {
		return windowTitle;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public State getUserState() {
		return state;
	}

	public void setUserState(State userState) {
		setOldState(state);
		this.state = userState;
	}

	public State getOldState() {
		return oldState;
	}

	public void setOldState(State oldState) {
		this.oldState = oldState;
	}
}
