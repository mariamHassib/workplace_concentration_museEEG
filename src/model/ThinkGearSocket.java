/* This provides a simple socket connector to the NeuroSky MindWave ThinkGear
 * connector. For more info visit
 * http://crea.tion.to/processing/thinkgear-java-socket No warranty or any
 * stuffs like that. Have fun! Andreas Borg borg@elevated.to
 * 
 * (c) 2010 This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version. This library is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * @author Andreas Borg, borg@elevated.to
 * 
 * @modified June, 2011
 * 
 * @version 1.0
 * 
 * This library is following the same design as the one developed by Jorge C. S.
 * Cardoso for the MindSet device. The MindWave device can communicate to a
 * socket over JSON instead of the serial port. That makes it easier and tidier
 * to talk between the device and Java. For instructions on how to use the
 * callback listeners please refer to
 * http://jorgecardoso.eu/processing/MindSetProcessing/
 * 
 * 
 * Data is passed back to the application via the following callback methods:
 * 
 * public void attentionEvent(int attentionLevel) Returns the current attention
 * level [0, 100]. Values in [1, 20] are considered strongly lowered. Values in
 * [20, 40] are considered reduced levels. Values in [40, 60] are considered
 * neutral. Values in [60, 80] are considered slightly elevated. Values in [80,
 * 100] are considered elevated.
 * 
 * public void meditationEvent(int meditationLevel) Returns the current
 * meditation level [0, 100]. The interpretation of the values is the same as
 * for the attentionLevel.
 * 
 * public void poorSignalEvent(int signalLevel) Returns the signal level [0,
 * 200]. The greater the value, the more noise is detected in the signal. 200 is
 * a special value that means that the ThinkGear contacts are not touching the
 * skin.
 * 
 * public void eegEvent(int delta, int theta, int low_alpha, int high_alpha, int
 * low_beta, int high_beta, int low_gamma, int mid_gamma) </code><br> Returns
 * the EEG data. The values have no units.
 * 
 * public void rawEvent(int []) Returns the the current 512 raw signal samples
 * [-32768, 32767]. */

package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import view.Strings;

public class ThinkGearSocket implements Runnable {

	private static final Logger l = Logger.getLogger(MainController.class.getName());
	public EEG parent;
	public Socket neuroSocket;
	public OutputStream outStream;
	public InputStream inStream;
	public BufferedReader stdIn;
	private Method poorSignalEventMethod = null;
	private Method blinkEventMethod = null;
	private Method eegEventMethod = null;
	private Method rawEventMethod = null;
	private Method esenseEventMethod = null;
	private Method famEventMethod = null;
	private Method meEventMethod = null;
	public String appName = "";
	public String appKey = "";
	private Thread t;
	private int raw[] = new int[512];
	private int index = 0;
	public final static String VERSION = "1.0";
	private boolean running = true;

	public ThinkGearSocket(EEG _parent, String _appName, String _appKey) {
		this(_parent);
		appName = _appName;// these were mentioned in the documentation as
							// required, but test prove they are not.
		appKey = _appKey;
	}

	public ThinkGearSocket(EEG _parent) {
		parent = _parent;
		try {
			esenseEventMethod = parent.getClass().getMethod("esenseEvent", new Class[] { int.class, int.class });
		} catch (Exception e) {
			l.log(Level.WARNING, "esenseEvent() method not defined. ");
		}
		try {
			poorSignalEventMethod = parent.getClass().getMethod("poorSignalEvent", new Class[] { int.class });
		} catch (Exception e) {
			l.log(Level.WARNING, "poorSignalEvent() method not defined. ");
		}
		try {
			blinkEventMethod = parent.getClass().getMethod("blinkEvent", new Class[] { int.class });
		} catch (Exception e) {
			l.log(Level.WARNING, "blinkEvent() method not defined. ");
		}
		try {
			eegEventMethod = parent.getClass().getMethod("eegEvent", new Class[] { int.class, int.class, int.class,
					int.class, int.class, int.class, int.class, int.class });
		} catch (Exception e) {
			l.log(Level.WARNING, "eegEvent() method not defined. ");
		}
		try {
			rawEventMethod = parent.getClass().getMethod("rawEvent", new Class[] { int[].class });
		} catch (Exception e) {
			l.log(Level.WARNING, "rawEvent() method not defined. ");
		}
		try {
			famEventMethod = parent.getClass().getMethod("famEvent", new Class[] { int.class });
		} catch (Exception e) {
			l.log(Level.WARNING, "famEvent() method not defined. ");
		}
		try {
			meEventMethod = parent.getClass().getMethod("meEvent", new Class[] { int.class });
		} catch (Exception e) {
			l.log(Level.WARNING, "meEvent() method not defined. ");
		}
	}

	public boolean isRunning() {
		return running;
	}

	/** return the version of the library.
	 * 
	 * @return String */
	public static String version() {
		return VERSION;
	}

	public void start() throws ConnectException {
		try {
			neuroSocket = new Socket("127.0.0.1", 13854);
		} catch (ConnectException e) {
			l.log(Level.INFO, Strings.THINKGEAR_NOT_CONNECTED.string);
			running = false;
			throw e;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			inStream = neuroSocket.getInputStream();
			outStream = neuroSocket.getOutputStream();
			stdIn = new BufferedReader(new InputStreamReader(neuroSocket.getInputStream()));
			running = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (appName != "" && appKey != "") {
			JSONObject appAuth = new JSONObject();
			try {
				appAuth.put("appName", appName);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			try {
				appAuth.put("appKey", appKey);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			sendMessage(appAuth.toString());
			l.log(Level.INFO, "appAuth" + appAuth);
		}

		JSONObject format = new JSONObject();
		try {
			format.put("enableRawOutput", true);
		} catch (JSONException e) {
			l.log(Level.WARNING, "raw error");
			e.printStackTrace();
		}
		try {
			format.put("format", "Json");
		} catch (JSONException e) {
			l.log(Level.WARNING, "json error");
			e.printStackTrace();
		}
		sendMessage(format.toString());
		t = new Thread(this);
		t.start();

	}

	public void stop() {
		if (running) {
			t.interrupt();
			try {
				neuroSocket.close();
				inStream.close();
				outStream.close();
				stdIn.close();
				stdIn = null;
			} catch (IOException e) {
				e.printStackTrace();
				l.log(Level.WARNING, "Socket close issue");
			}
		}
		running = false;
	}

	public void sendMessage(String msg) {
		PrintWriter out = new PrintWriter(outStream, true);
		// System.out.println("sendmsg");
		out.println(msg);
	}

	@Override
	public void run() {
		if (running && neuroSocket.isConnected()) {
			String userInput;

			try {
				while ((userInput = stdIn.readLine()) != null) {

					String[] packets = userInput.split("/\r/");
					for (int s = 0; s < packets.length; s++) {
						if (packets[s].indexOf("{") > -1) {
							JSONObject obj = new JSONObject(packets[s]);
							parsePacket(obj);
						}
						// String name = obj.get("name").toString();
					}
				}
			} catch (IOException | JSONException e) {
				l.log(Level.WARNING, e.getMessage()
						+ "\nFor some reason stdIn throws error even if closed. Maybe it takes a cycle to close properly?");
				e.printStackTrace();
			}
			// parent.delay(50);
		} else {
			running = false;
		}
	}

	private void triggerEsenseEvent(int attention, int meditation) {
		if (esenseEventMethod != null) {
			try {
				esenseEventMethod.invoke(parent, new Object[] { attention, meditation });
			} catch (Exception e) {
				l.log(Level.WARNING, "Disabling esenseEvent()  because of an error.");
				e.printStackTrace();
				esenseEventMethod = null;
			}
		}
	}

	private void triggerPoorSignalEvent(int poorSignalLevel) {
		if (poorSignalEventMethod != null) {
			try {
				poorSignalEventMethod.invoke(parent, new Object[] { poorSignalLevel });
			} catch (Exception e) {
				l.log(Level.WARNING, "Disabling meditationEvent()  because of an error.");
				e.printStackTrace();
				poorSignalEventMethod = null;
			}
		}
	}

	private void triggerBlinkEvent(int blinkStrength) {
		if (blinkEventMethod != null) {
			try {
				blinkEventMethod.invoke(parent, new Object[] { blinkStrength });
			} catch (Exception e) {
				l.log(Level.WARNING, "Disabling blinkEvent()  because of an error.");
				e.printStackTrace();
				blinkEventMethod = null;
			}
		}
	}

	private void triggerEEGEvent(int delta, int theta, int low_alpha, int high_alpha, int low_beta, int high_beta,
			int low_gamma, int mid_gamma) {
		if (eegEventMethod != null) {
			try {
				eegEventMethod.invoke(parent, new Object[] { delta, theta, low_alpha, high_alpha, low_beta, high_beta,
						low_gamma, mid_gamma });
			} catch (Exception e) {
				l.log(Level.WARNING, "Disabling eegEvent()  because of an error.");
				e.printStackTrace();
				eegEventMethod = null;
			}
		}
	}

	private void triggerRawEvent(int[] values) {
		if (rawEventMethod != null) {
			try {
				rawEventMethod.invoke(parent, new Object[] { values });
			} catch (Exception e) {
				l.log(Level.WARNING, "Disabling rawEvent()  because of an error.");
				e.printStackTrace();
				rawEventMethod = null;
			}
		}
	}

	private void triggerFamEvent(int familiarity) {
		if (famEventMethod != null) {
			try {
				famEventMethod.invoke(parent, new Object[] { familiarity });
			} catch (Exception e) {
				l.log(Level.WARNING, "Disabling famEvent() because of an error.");
				e.printStackTrace();
				famEventMethod = null;
			}
		}
	}

	private void triggerMEEvent(int mentalEffort) {
		if (meEventMethod != null) {
			try {
				meEventMethod.invoke(parent, new Object[] { mentalEffort });
			} catch (Exception e) {
				l.log(Level.WARNING, "Disabling meEvent() because of an error.");
				e.printStackTrace();
				meEventMethod = null;
			}
		}
	}

	private void parsePacket(JSONObject data) {
		@SuppressWarnings("rawtypes")
		Iterator itr = data.keys();
		while (itr.hasNext()) {
			Object e = itr.next();
			String key = e.toString();
			try {
				if (key.matches("poorSignalLevel")) {
					triggerPoorSignalEvent(data.getInt(e.toString()));
				}
				if (key.matches("rawEeg")) {
					int rawValue = (Integer) data.get("rawEeg");
					raw[index] = rawValue;
					index++;
					if (index == 512) {
						index = 0;
						int rawCopy[] = new int[512];
						// parent.arrayCopy(raw, rawCopy);
						triggerRawEvent(rawCopy);
					}
				}
				if (key.matches("blinkStrength")) {
					triggerBlinkEvent(data.getInt(e.toString()));
				}
				if (key.matches("eSense")) {
					JSONObject esense = data.getJSONObject("eSense");
					triggerEsenseEvent(esense.getInt("attention"), esense.getInt("meditation"));
				}
				if (key.matches("eegPower")) {
					JSONObject eegPower = data.getJSONObject("eegPower");
					triggerEEGEvent(eegPower.getInt("delta"), eegPower.getInt("theta"), eegPower.getInt("lowAlpha"),
							eegPower.getInt("highAlpha"), eegPower.getInt("lowBeta"), eegPower.getInt("highBeta"),
							eegPower.getInt("lowGamma"), eegPower.getInt("highGamma"));
				}
				if (key.matches("mentalEffort")) {
					triggerMEEvent(data.getInt(e.toString()));
				}
				if (key.matches("familiarity")) {
					triggerFamEvent(data.getInt(e.toString()));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
