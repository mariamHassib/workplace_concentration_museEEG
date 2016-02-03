package model;

import java.util.logging.Level;
import java.util.logging.Logger;

import view.Strings;

public class Feedback {

	private static final Logger l = Logger.getLogger(Feedback.class.getName());
	private String description;
	private Work work;

	public Feedback(Work work, String description) {
		this.work = work;
		this.description = description;
	}

	public enum Event {
		SCHEDULED_MEETING, UNSCHEDULED_MEETING, OTHER;
	}

	public enum Work {
		CENTRAL(Strings.TAG1_TOOLTIP.string), PERIPHERAL(Strings.TAG2_TOOLTIP.string), META(Strings.TAG3_TOOLTIP.string);
		
		public String tooltip;
		
		Work (String tooltip){
			this.tooltip = tooltip;
		}
	}

	public void change(Work work, String description) {
		setDescription(description);
		setWork(work);
		l.log(Level.INFO, "Work: {0} | Description: {1}", new Object[] { work.toString().toLowerCase(), description });
	}

	// ------------------ GETTER + SETTER --------------------------
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}
}
