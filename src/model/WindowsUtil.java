package model;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

import model.Activity.State;

public class WindowsUtil extends Observable implements Runnable {

	private MainController main;
	private static final int MAX_TITLE_LENGTH = 1024;
	private static final Logger lMain = Logger.getLogger(MainController.class.getName());
	private State state, newState;
	private String currentProcess, currentTitle;

	public WindowsUtil(MainController main) {
		this.main = main;
	}

	/** Returns the title of the active window.
	 * @return title */
	private static String getActiveWindowTitle() {
		HWND foregroundWindow = User32DLL.GetForegroundWindow(); // User32.INSTANCE.GetForegroundWindow();
		// int titleLength =
		// User32.INSTANCE.GetWindowTextLength(foregroundWindow) + 1;
		int titleLength = MAX_TITLE_LENGTH * 2;
		char[] buffer = new char[titleLength];
		User32DLL.GetWindowTextW(foregroundWindow, buffer, titleLength);
		String title = Native.toString(buffer);
		return title;
	}

	/** Returns the process name of the active window.
	 * @return process name */
	private static String getActiveWindowProcess() {
		char[] buffer = new char[MAX_TITLE_LENGTH * 2];
		PointerByReference pointer = new PointerByReference();
		HWND foregroundWindow = User32DLL.GetForegroundWindow();
		User32DLL.GetWindowThreadProcessId(foregroundWindow, pointer);
		Pointer process = Kernel32.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false,
				pointer.getValue());
		Psapi.GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
		String processName = Native.toString(buffer);
		return processName;
	}

	static class Psapi {
		static {
			Native.register("psapi");
		}

		public static native int GetModuleBaseNameW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);
	}

	static class Kernel32 {
		static {
			Native.register("kernel32");
		}

		public static int PROCESS_QUERY_INFORMATION = 0x0400;
		public static int PROCESS_VM_READ = 0x0010;

		public static native Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
	}

	static class User32DLL {
		static {
			Native.register("user32");
		}

		public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);

		public static native HWND GetForegroundWindow();

		public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
	}

	// ------------------STATE------------------------

	public interface Kernel32_ extends StdCallLibrary {
		Kernel32_ INSTANCE = (Kernel32_) Native.loadLibrary("kernel32", Kernel32_.class);

		/** Retrieves the number of milliseconds that have elapsed since the
		 * system was started.
		 * @see http://msdn2.microsoft.com/en-us/library/ms724408.aspx
		 * @return number of milliseconds that have elapsed since the system was
		 *         started. */
		public int GetTickCount();
	};

	public interface User32 extends StdCallLibrary {
		User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

		/** Contains the time of the last input.
		 * @see http
		 *      ://msdn.microsoft.com/library/default.asp?url=/library/en-us/
		 *      winui/winui/windowsuserinterface/userinput
		 *      /keyboardinput/keyboardinputreference/keyboardinputstructures/
		 *      lastinputinfo.asp */
		public static class LASTINPUTINFO extends Structure {
			public int cbSize = 8;
			// / Tick count of when the last input event was received.
			public int dwTime;

			@SuppressWarnings("rawtypes")
			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] { "cbSize", "dwTime" });
			}
		}

		/** Retrieves the time of the last input event.
		 * @see http
		 *      ://msdn.microsoft.com/library/default.asp?url=/library/en-us/
		 *      winui/winui/windowsuserinterface/userinput
		 *      /keyboardinput/keyboardinputreference/keyboardinputfunctions/
		 *      getlastinputinfo.asp
		 * @return time of the last input event, in milliseconds */
		public boolean GetLastInputInfo(LASTINPUTINFO result);
	};

	/** Get the amount of milliseconds that have elapsed since the last input
	 * event (mouse or keyboard)
	 * @return idle time in milliseconds */
	public static int getIdleTimeMillisWin32() {
		User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
		User32.INSTANCE.GetLastInputInfo(lastInputInfo);
		return Kernel32_.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
	}

	// ---------------------------------------------------

	@Override
	public void run() {
		if (!System.getProperty("os.name").contains("Windows")) {
			System.err.println("Only implemented on Windows");
			System.exit(1);
		}
		String lastTitle = "";
		// String lastProcess = "";
		// long lastChange = System.currentTimeMillis();
		int idleSec;

		while (true) {
			// State
			idleSec = getIdleTimeMillisWin32() / 1000;
			newState = idleSec < 30 ? State.ACTIVE : idleSec > 5 * 60 ? State.AWAY : State.IDLE;
			if (newState != state) {
				main.getActivity().change(newState);
				state = newState;
			}
			currentTitle = getActiveWindowTitle();
			currentProcess = getActiveWindowProcess();

			// Log Process Infos
			if (!lastTitle.equals(currentTitle)) { // (!lastProcess.equals(currentProcess)){
				// long change = System.currentTimeMillis();
				// long time = (change - lastChange) / 1000;
				main.getActivity().change(state, currentProcess, currentTitle);
				// lastChange = change;
				lastTitle = currentTitle;
				// lastProcess = currentProcess;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				lMain.log(Level.WARNING, e.getMessage());
			}
		}
	}

	public State getNewState() {
		return newState;
	}

	public String getActiveProcess() {
		if (currentProcess != null)
			return currentProcess;
		else
			return "unknown";
	}
}