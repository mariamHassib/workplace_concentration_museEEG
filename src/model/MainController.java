package model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import view.View;
import javafx.application.Application;
import javafx.stage.Stage;
import model.Activity.State;
import model.Feedback.Work;

public class MainController extends Application {

	public static FileHandler fhWin, fhEEG, fhTask, fhMain;
	private static final Logger lMain = Logger.getLogger(MainController.class.getName());
	private static final Logger lEEG = Logger.getLogger(EEG.class.getName());
	private static final Logger lWin = Logger.getLogger(Activity.class.getName());
	private static final Logger lTask = Logger.getLogger(Feedback.class.getName());

	ThinkGearSocket neuroSocket;
	View view;
	WindowsUtil win;
	Activity activity;
	EEG eeg;
	MuseOscServer muse;
	Feedback feedback;

	@Override
	public void start(Stage primaryStage) {
		view = new View(this);
		eeg = new EEG();
		muse=new MuseOscServer();
		eeg.addObserver(view);
	}

	public static void main(String[] args) {
		launch(args);
	}

	/** Initializes the Logger. */
	public void initLogger() {
		try {
			fhEEG = new FileHandler("src/view/log/eeg.csv"); // %g%u  // ("src/view/log/" + getDate() + ".eeg.csv")
			fhWin = new FileHandler("src/view/log/win.csv");
			fhTask = new FileHandler("src/view/log/task.csv");
			fhMain = new FileHandler("src/view/log/main.log");
		} catch (SecurityException e) {
			lMain.log(Level.WARNING, e.toString());
		} catch (IOException e) {
			lMain.log(Level.WARNING, e.toString());
		}
		lEEG.addHandler(fhEEG);
		lWin.addHandler(fhWin);
		lTask.addHandler(fhTask);
		lMain.addHandler(fhMain);
		fhEEG.setFormatter(new LogFormatter());
		fhWin.setFormatter(new LogFormatter());
		fhTask.setFormatter(new LogFormatter());
		fhMain.setFormatter(new SimpleFormatter());
	}

	public void startEEGLogging() {
		// eeg = new EEG();
		// eeg.addObserver(view);
		neuroSocket = new ThinkGearSocket(eeg);
		try {
			neuroSocket.start();
			lMain.log(Level.SEVERE, "Socket start");
			
			//view.changeStatus(Strings.THINKGEAR_CONNECTED.string, 3);
		} catch (Exception e) {
			 //view.changeStatus(Strings.THINKGEAR_NOT_CONNECTED.string, 3);
		}
	}

	public void startPALogging() {
		// PROCESS-LOGGING
		activity = new Activity(State.UNKNOWN, "", "");
		activity.addObserver(view);
		new Thread(new WindowsUtil(this)).start();

		// FEEDBACK-LOGGING
		feedback = new Feedback(Work.META, "");
	}

	// ----------- EEG ------------------

	@Override
	public void stop() {
		neuroSocket.stop();
		try {
			super.stop();
			// eeg.deleteObservers();
		} catch (Exception e) {
			lMain.log(Level.SEVERE, "Socketstop failed", e);
		}
	}

	// ------------ GETTER + SETTER -------------------------

	public WindowsUtil getWindows() {
		return win;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public EEG getEeg() {
		return eeg;
	}

	public void setEeg(EEG eeg) {
		this.eeg = eeg;
	}

	public Feedback getFeedback() {
		return feedback;
	}

	public void setFeedback(Feedback feedback) {
		this.feedback = feedback;
	}

	public String getDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
}