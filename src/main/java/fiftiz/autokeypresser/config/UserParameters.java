package fiftiz.autokeypresser.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class UserParameters
{
	public static final String PARAMS_FILE_NAME = "params.ini";
	
	public static final String START_STOP_AUTOPRESS_KEY_KEY = "START_STOP_AUTOPRESS_KEY";
	public static final String DEFAULT_AUTOPRESS_DELAY_KEY = "DEFAULT_AUTOPRESS_DELAY";
	
	private Map<String, String> parameters;

	public UserParameters()
	{
		parameters = new HashMap<String, String>();
	}

	public boolean readParametersFile()
	{
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(PARAMS_FILE_NAME);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
			String readLine;

			try {
				while ((readLine = reader.readLine()) != null) {
					String[] readParameter = readLine.split("=");
					if (readParameter.length == 2) {
						parameters.put(readParameter[0], readParameter[1]);
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			try {
				reader.close();
				return true;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return false;

	}

	public String getParameter(String key)
	{
		return parameters.get(key);
	}
}
