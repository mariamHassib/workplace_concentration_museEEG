package view;

public enum Strings {

	// @formatter:off
	THINKGEAR_CONNECTED("ThinkGear is connected/running."),
	THINKGEAR_NOT_CONNECTED("ThinkGear is not connected/running!"),
	POOR_SIGNAL("Poor Signal"),
	
	HI("Hi! What are you doing?"),
	IDLE("Hey there! You didn't use your PC for a while."),
	AWAY("I noticed you were gone.\nWhat did you do?"),
	
	START("START"),
	STOP("STOP"), 
	YOU_ARE("You are currently "),
	WINDOW_TITLE("Logger"),
	GRAPH("View graph in browser"),
	
	TAG0_TOOLTIP("Please tag your current task!"),
	TAG1_TOOLTIP("A task which is part of your major/substantial work."),
	TAG2_TOOLTIP("A task which is not part of your major/substantial work \n (i.e. attending a meeting, writing emails,...)"),
	TAG3_TOOLTIP("The management of your working tasks \n(i.e. coordinating/checking activities, \norganizing your email/desk, \ncatching up with workmates,...)"),
	SIGNAL_TOOLTIP("Wave quality"),
	
	HTML("log/htmlfile.html"), 
	;
	// @formatter:on

	public String string;

	Strings(String string) {
		this.string = new String(string);
	}

	@Override
	public String toString() {
		return string;
	}
}
