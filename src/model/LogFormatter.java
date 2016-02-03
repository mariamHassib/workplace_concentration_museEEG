package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/** Formatter takes a LogRecord and converts it to CSV content.
 * @author Susanne */
class LogFormatter extends Formatter {

	/** column separator */
	private String s = ",";

	@Override
	public String format(LogRecord rec) {
		StringBuffer buf = new StringBuffer(1000);
		buf.append(formatTime(rec.getMillis()) + s);
		int length = rec.getParameters().length;
		for (int i = 0; i < length; i++) {
			Object p = rec.getParameters()[i];
			buf.append(String.valueOf(p));
			if (i < length - 1)
				buf.append(s);
		}
		buf.append("\n");
		return buf.toString();
	}

	private String formatTime(long millisecs) {
		return new SimpleDateFormat("yyMMddHHmmss").format(new Date(millisecs));
	}

	@Override
	public String getHead(Handler h) {
		String tableTitles = "???";
		if (h.equals(MainController.fhWin))
			tableTitles = "userActivity" + s + "activeProcess" + s + "windowTitle";
		if (h.equals(MainController.fhTask))
			tableTitles = "workingSphere" + s + "description";
		if (h.equals(MainController.fhEEG))
			tableTitles = "signal" + s + "taskEngagement" + s + "attention" + s + "meditation";
		return "time" + s + tableTitles + "\n";
	}

	@Override
	public String getTail(Handler h) {
		return "";
	}
}