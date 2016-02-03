package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WinProcesses {

	public static String getListOfProcesses() throws IOException {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		List<String> command = new ArrayList<String>();
		command.add("WMIC");
		command.add("process");
		try {
			ProcessBuilder builder = new ProcessBuilder(command);
			Process process = builder.start();
			is = process.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			StringBuilder sw = new StringBuilder();

			String line;
			while ((line = br.readLine()) != null) {
				sw.append(line.trim());
				sw.append(System.lineSeparator());
			}
			return sw.toString();
		} finally {
			if (br != null)
				br.close();
			if (isr != null)
				isr.close();
			if (is != null)
				is.close();
		}
	}
}
