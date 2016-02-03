package model;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EEG extends Observable {

	private static final Logger lEEG = Logger.getLogger(EEG.class.getName());

	/** A quantifier of the quality of the brainwave signal. This is an integer
	 * value that is generally in the range of 0 to 200, with 0 indicating a
	 * good signal and 200 indicating an off-head state. //
	 * Will be logged as signal/2 to adjust the value to the coordinate system. */
	int signal;

	int attention;
	int meditation;
	/** eSense. A container for the eSense™ attributes. These are integer
	 * values between 0 and 100, where 0 is perceived as a lack of that
	 * attribute and 100 is an excess of that attribute. */
	int[] eSenseValues = new int[] { attention, meditation };

	float taskEngagement;

	/** 0.1-3Hz Deep, dreamless sleep, non-REM sleep, unconscious */
	int delta;
	/** 4-7Hz Intuitive, creative, recall, fantasy, imaginary, dream */
	int theta;

	int lowAlpha;
	int highAlpha;
	/** 8-12Hz Relaxed (but not drowsy) tranquil, conscious */
	int alpha;

	/** 12-15Hz Formerly SMR, relaxed yet focused, integrated */
	int lowBeta;
	/** 21-30Hz Alertness, agitation */
	int highBeta;
	int beta;

	int lowGamma;
	int midGamma;
	/** eegPower. A container for the EEG powers. These may be either integer or
	 * floating-point values. */
	int[] eegValues = new int[] { delta, theta, lowAlpha, highAlpha, lowBeta, highBeta, lowGamma, midGamma };

	/** The raw data reading off the forehead sensor. This may be either an
	 * integer or a floating-point value. / rawEegMulti. A container for
	 * multichannel raw EEG data. These may be either integer or floating-point
	 * values. m ch1-8. The raw data from channel 1-8. */
	int[] raw;

	/** (BETA) The mentalEffort of doing a task. This is a decimal number. */
	int mentalEffort;
	/** (BETA) The familiarity of doing a task. This is a decimal number. */
	int familiarity;
	int[] betaValues = new int[] { mentalEffort, familiarity };

	/** The strength of a detected blink. This is an integer in the range of
	 * 0-255. */
	int blinkStrength;

	public void poorSignalEvent(int sig) {
		setSignal(sig);
		setChanged();
		notifyObservers(sig);
	}

	public void blinkEvent(int blinkStrength) {
		setBlinkStrength(blinkStrength);
	}

	public void esenseEvent(int attention, int meditation) {
		setAttention(attention);
		setMeditation(meditation);
	}

	public void eegEvent(int delta, int theta, int low_alpha, int high_alpha, int low_beta, int high_beta,
			int low_gamma, int mid_gamma) {
		setDelta(delta);
		setTheta(theta);
		setLowAlpha(low_alpha);
		setHighAlpha(high_alpha);
		setAlpha();
		setLowBeta(low_beta);
		setHighBeta(high_beta);
		setBeta();
		setLowGamma(low_gamma);
		setMidGamma(mid_gamma);
		setTaskEngagement();
		lEEG.log(Level.INFO,
				"signal: {0} | taskEngagment: {1} | attention: {2} | meditation: {3} | beta: {4} | alpha: {5} | theta: {6}",
				new Object[] { signal, getTaskEngagement(), attention, meditation, beta, alpha, theta });
	}

	public void rawEvent(int[] raw) {
		setRaw(raw);
		for (int i = 0; i < raw.length; i++) {
			// lMain.log(Level.INFO, "rawEvent Level " + i + ": " + raw[i]);
		}
	}

	public void meEvent(int mentalEffort) {
		setMentalEffort(mentalEffort);
	}

	public void famEvent(int familiarity) {
		setFamiliarity(familiarity);
	}

	// ------------------ GETTER + SETTER --------------------------

	public int getSignal() {
		return signal;
	}

	public void setSignal(int signal) {
		this.signal = signal/2;
	}

	public int getAttention() {
		return attention;
	}

	public void setAttention(int attention) {
		this.attention = attention;
	}

	public int getMeditation() {
		return meditation;
	}

	public void setMeditation(int meditation) {
		this.meditation = meditation;
	}

	public int[] geteSenseValues() {
		return eSenseValues;
	}

	public void seteSenseValues(int[] eSenseValues) {
		this.eSenseValues = eSenseValues;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

	public int getTheta() {
		return theta;
	}

	public void setTheta(int theta) {
		this.theta = theta;
	}

	public int getLowAlpha() {
		return lowAlpha;
	}

	public void setLowAlpha(int lowAlpha) {
		this.lowAlpha = lowAlpha;
	}

	public int getHighAlpha() {
		return highAlpha;
	}

	public void setHighAlpha(int highAlpha) {
		this.highAlpha = highAlpha;
	}

	public int getLowBeta() {
		return lowBeta;
	}

	public void setLowBeta(int lowBeta) {
		this.lowBeta = lowBeta;
	}

	public int getHighBeta() {
		return highBeta;
	}

	public void setHighBeta(int highBeta) {
		this.highBeta = highBeta;
	}

	public int getLowGamma() {
		return lowGamma;
	}

	public void setLowGamma(int lowGamma) {
		this.lowGamma = lowGamma;
	}

	public int getMidGamma() {
		return midGamma;
	}

	public void setMidGamma(int midGamma) {
		this.midGamma = midGamma;
	}

	public int[] getEegValues() {
		return eegValues;
	}

	public void setEegValues(int[] eegValues) {
		this.eegValues = eegValues;
	}

	public int[] getRaw() {
		return raw;
	}

	public void setRaw(int[] raw) {
		this.raw = raw;
	}

	public int getMentalEffort() {
		return mentalEffort;
	}

	public void setMentalEffort(int mentalEffort) {
		this.mentalEffort = mentalEffort;
	}

	public int getFamiliarity() {
		return familiarity;
	}

	public void setFamiliarity(int familiarity) {
		this.familiarity = familiarity;
	}

	public int[] getBetaValues() {
		return betaValues;
	}

	public void setBetaValues(int[] betaValues) {
		this.betaValues = betaValues;
	}

	public void setBeta() {
		this.beta = (getLowBeta() + getHighBeta()) / 2;
	}

	public float getBeta() {
		return beta;
	}

	public int getBlinkStrength() {
		return blinkStrength;
	}

	public void setBlinkStrength(int blinkStrength) {
		this.blinkStrength = blinkStrength;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha() {
		this.alpha = (getLowAlpha() + getHighAlpha()) / 2;
	}

	public float getTaskEngagement() {
		return taskEngagement;
	}

	public void setTaskEngagement() {
		this.taskEngagement =  100 *(getBeta() / (getAlpha() + getTheta()));
	}
}
